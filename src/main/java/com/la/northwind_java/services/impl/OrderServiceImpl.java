
package com.la.northwind_java.services.impl;



import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import com.la.northwind_java.config.exceptions.DatabaseException;
import com.la.northwind_java.config.exceptions.InvalidRequestException;
import com.la.northwind_java.dtos.OrderCreateDTO;
import com.la.northwind_java.dtos.OrderDTO;
import com.la.northwind_java.dtos.OrderDetailsDTO;
import com.la.northwind_java.dtos.OrderUpdateDTO;
import com.la.northwind_java.mappers.OrderMapper;
import com.la.northwind_java.models.Customer;
import com.la.northwind_java.models.InventoryTransaction;
import com.la.northwind_java.models.InventoryTransactionType;
import com.la.northwind_java.models.Order;
import com.la.northwind_java.models.OrderDetails;
import com.la.northwind_java.models.OrderStatus;
import com.la.northwind_java.models.Product;
import com.la.northwind_java.repositories.CustomerRepository;
import com.la.northwind_java.repositories.InventoryTransactionRepository;
import com.la.northwind_java.repositories.OrderRepository;
import com.la.northwind_java.repositories.OrderStatusRepository;
import com.la.northwind_java.repositories.ProductRepository;
import com.la.northwind_java.repositories.InventoryTransactionTypeRepository;
import com.la.northwind_java.services.OrderService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

	//Estado por defecto para un pedido recien creado ( ver seed order_status: = 'New')
	private static final byte DEFAULT_STATUS_ID = 0;
	
	private final OrderRepository orderRepository;
	private final OrderMapper orderMapper;
	private final CustomerRepository customerRepository;
	private final ProductRepository productRepository;
	private final OrderStatusRepository orderStatusRepository;
	private final InventoryTransactionRepository inventoryTransactionRepository;
	private final InventoryTransactionTypeRepository inventoryTransactionTypeRepository;
	
	
	@Override
	@Cacheable(value = "orders", key = "#page + '-' + #size + '-' + #customerId + '-' + #employeeId + '-' + #status + '-' + #sortBy + '-' + #sortDirection")
	public Page<OrderDTO> getAllOrders(int page, int size, Integer customerId, Integer employeeId, String status, String sortBy, String sortDirection){
		Sort sort = sortDirection.equalsIgnoreCase("desc")? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
		Pageable pageable = PageRequest.of(page, size, sort);
		Page<Order> orders = orderRepository.findOrders(pageable, customerId, employeeId, status);
		return orders.map(orderMapper::toOrderDTO);
		
	}
	
	/**
	 * Crea un pedido/factura a partir de un cliente y una lista de lineas
	 * (producto + cantidad). Valida stock disponible por producto (calculado
	 * a partir del historial de InventoryTransaction, ya que products no
	 * guarda una cantidad en stock directamente), calcula subtotal + impuestos
	 * +envio, y al confirmar genera una transaction de inventario tipo 'Sold'
	 * por cada línea para descontar el stock.
	 * 
	 */
	
	@Override
	@Transactional
	@CacheEvict(value = "orders", allEntries = true)
	public OrderDTO createOrder(OrderCreateDTO orderCreateDTO) {
		
		if(orderCreateDTO.getOrderDetails() == null || orderCreateDTO.getOrderDetails().isEmpty()) {
			throw new InvalidRequestException("El pedido debe tener al menos un producto.");
		}
		Customer customer =  customerRepository.findById(orderCreateDTO.getCustomerId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"Cliente no encontrado con id: " + orderCreateDTO.getCustomerId()));
		OrderStatus status = orderStatusRepository.findById(
				orderCreateDTO.getStatusId() != null ? orderCreateDTO.getStatusId().byteValue() : DEFAULT_STATUS_ID)
				.orElseThrow(() -> new ResourceNotFoundException("Estado de pedido no encontrado"));
		
		InventoryTransactionType soldType = inventoryTransactionTypeRepository.findByTypeName("Sold")
				.orElseThrow(() -> new ResourceNotFoundException(
						"No se encontro el tipo de transacción de inventario 'Sold'. Verificar seed de la base."));
		
		Order order = Order.builder()
		.customer(customer)
		.status(status)
		.orderDate(orderCreateDTO.getOrderDate() != null ? orderCreateDTO.getOrderDate() : LocalDateTime.now())
		.shipName(orderCreateDTO.getShipName())
		.shipAddress(orderCreateDTO.getShipAddress())
		.shipCity(orderCreateDTO.getShipCity())
		.shipStateProvince(orderCreateDTO.getShipStateProvince())
		.shipZipPostalCode(orderCreateDTO.getShipPostalCode())
		.shipCountryRegion(orderCreateDTO.getShipCountryRegion())
		.paymentType(orderCreateDTO.getPaymentType())
		.notes(orderCreateDTO.getNotes())
		.taxRate(orderCreateDTO.getTaxRate())
		.shippingFee(orderCreateDTO.getShippingFee() != null ? orderCreateDTO.getShippingFee() : BigDecimal.ZERO)
		.orderDetails(new ArrayList<>())
		.build();
		
		List<InventoryTransaction> stockMovements = new ArrayList<>();
		BigDecimal subtotal = BigDecimal.ZERO;
		
		for(OrderDetailsDTO line : orderCreateDTO.getOrderDetails()) {
			if(line.getProductId() == null || line.getQuantity() == null || line.getQuantity() <= 0) {
				throw new InvalidRequestException("Cada linea del pedido necesita un producto y una cantidad mayor a 0.");
			}
			Product product = productRepository.findById(line.getProductId())
					.orElseThrow( ()-> new ResourceNotFoundException (
							"Producto no encontrado con id: " + line.getProductId()));
			
			if(Boolean.TRUE.equals(product.getDiscontinued())) {
				throw new InvalidRequestException("El producto'"+ product.getProductName() + "' esta descontinuado.");
			}
			long availableStock = inventoryTransactionRepository.getAvailableStock(product.getProductID());
			if(line.getQuantity() > availableStock) {
				throw new InvalidRequestException(
						"stock insuficiente para '"+product.getProductName()+"'. Disponible: "
						+ availableStock + ", solicitado: "+ line.getQuantity());
			}
			BigDecimal unitPrice = line.getUnitPrice() != null ? line.getUnitPrice() : product.getListPrice();
			double discount = line.getDiscount() != null ? line.getDiscount() : 0.0;
			
			OrderDetails detail = OrderDetails.builder()
					.order(order)
					.product(product)
					.unitPrice(unitPrice)
					.quantity(BigDecimal.valueOf(line.getQuantity()))
					.discount(discount)
					.build();
			order.getOrderDetails().add(detail);
			
			BigDecimal lineTotal = unitPrice
					.multiply(BigDecimal.valueOf(line.getQuantity()))
					.multiply(BigDecimal.valueOf(1-discount));
			subtotal = subtotal.add(lineTotal);
			
			stockMovements.add(InventoryTransaction.builder()
					.transactionType(soldType)
					.product(product)
					.quantity(line.getQuantity())
					.customerOrder(order)
					.transactionModifiedDate(LocalDateTime.now())
					.comments("Venta")
					.build());
		}
		BigDecimal taxes;
		if(orderCreateDTO.getTaxRate() != null) {
			taxes = subtotal.multiply(BigDecimal.valueOf(orderCreateDTO.getTaxRate() / 100.0))
					.setScale(2, RoundingMode.HALF_UP);
		}else {
			taxes = orderCreateDTO.getTaxes() != null ? orderCreateDTO.getTaxes() : BigDecimal.ZERO;
		}
		order.setTaxes(taxes);
		try {
			Order savedOrder = orderRepository.save(order);
			//Recien aca tenemos el Order con ID, para poder linkear las 
			//transacciones de inventario (customer_order_id) y guardarlas.
			inventoryTransactionRepository.saveAll(stockMovements);
			return orderMapper.toOrderDTO(savedOrder);
			
		}catch(DataAccessException e) {
			throw new DatabaseException("Error al crear el pedido", e);
		}
		
	}
	@Override
	public OrderDTO getOrderById(Integer id) {
		return orderRepository.findById(id)
				.map(orderMapper::toOrderDTO)
				.orElseThrow(()->new ResourceNotFoundException("Order not found"));
	}
	@Override
	@Transactional
	@CacheEvict(value = "orders", allEntries = true)
	public OrderDTO updateOrder(Integer id, OrderUpdateDTO orderUpdateDTO) {
		Order order = orderRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found with id: "+ id));
		try {
			orderMapper.updateEntity(orderUpdateDTO, order);
			Order updatedOrder = orderRepository.save(order);
			return orderMapper.toOrderDTO(updatedOrder);
		}catch(DataAccessException e) {
			throw new DatabaseException("Error updating order with id: " + id, e);
		}
		
	}
	
	
	@Override
	@Transactional
	@CacheEvict(value = "orders", allEntries=true)
	public void deleteOrder(Integer id) {
		if(!orderRepository.existsById(id)) {
			throw new ResourceNotFoundException("Order not found");
		}
		orderRepository.deleteById(id);
	}
	
	
	
	
}













