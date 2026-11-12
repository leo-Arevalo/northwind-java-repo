
package com.la.northwind_java.services.impl;



import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.la.northwind_java.config.exceptions.DatabaseException;
import com.la.northwind_java.config.exceptions.InvalidRequestException;
import com.la.northwind_java.config.exceptions.ResourceNotFoundException;
import com.la.northwind_java.dtos.OrderCreateDTO;
import com.la.northwind_java.dtos.OrderDTO;
import com.la.northwind_java.dtos.OrderDetailsDTO;
import com.la.northwind_java.dtos.OrderUpdateDTO;
import com.la.northwind_java.dtos.payment.PaymentDetailsDTO;
import com.la.northwind_java.mappers.OrderMapper;
import com.la.northwind_java.mappers.OrderPaymentMapper;
import com.la.northwind_java.models.Customer;
import com.la.northwind_java.models.InventoryTransaction;
import com.la.northwind_java.models.InventoryTransactionType;
import com.la.northwind_java.models.Order;
import com.la.northwind_java.models.OrderDetails;
import com.la.northwind_java.models.OrderPayment;
import com.la.northwind_java.models.OrderStatus;
import com.la.northwind_java.models.PaymentMethod;
import com.la.northwind_java.models.Product;
import com.la.northwind_java.repositories.CustomerRepository;
import com.la.northwind_java.repositories.InventoryTransactionRepository;
import com.la.northwind_java.repositories.OrderRepository;
import com.la.northwind_java.repositories.OrderStatusRepository;
import com.la.northwind_java.repositories.ProductRepository;
import com.la.northwind_java.repositories.InventoryTransactionTypeRepository;
import com.la.northwind_java.repositories.OrderPaymentRepository;
import com.la.northwind_java.services.OrderService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

	//Estado por defecto para un pedido recien creado ( ver seed order_status: = 'New')
	private static final byte DEFAULT_STATUS_ID = 0;
	
	//Maximo de pagos por factura y etiqueta cuando se combinan metodos distintos
	private static final int MAX_PAYMENTS = 5;
	private static final String MIXED_PAYMENT_LABEL = "Mixto";
	
	private final OrderRepository orderRepository;
	private final OrderMapper orderMapper;
	private final CustomerRepository customerRepository;
	private final ProductRepository productRepository;
	private final OrderStatusRepository orderStatusRepository;
	private final InventoryTransactionRepository inventoryTransactionRepository;
	private final InventoryTransactionTypeRepository inventoryTransactionTypeRepository;
	private final OrderPaymentRepository orderPaymentRepository;
	private final OrderPaymentMapper orderPaymentMapper;
	
	
	@Override
	@Cacheable(value = "orders", key = "#page + '-' + #size + '-' + #customerId + '-' + #employeeId + '-' + #status + '-' + #sortBy + '-' + #sortDirection")
	public Page<OrderDTO> getAllOrders(int page, int size, Integer customerId, Integer employeeId, String status, String sortBy, String sortDirection){
		Sort sort = sortDirection.equalsIgnoreCase("desc")? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
		Pageable pageable = PageRequest.of(page, size, sort);
		Page<Order> orders = orderRepository.findOrders(pageable, customerId, employeeId, status);
		Page<OrderDTO> result = orders.map(orderMapper::toOrderDTO);
		attachPayments(result.getContent());
		return result;
		
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
		//Los pagos se validan primero (metodo, datos de cada uno y montos): si falta
		// el cupon de una tarjeta o el numero de operación, se corta antes de armar el
		// pedido. Que sumen el total se chequea mas abajo, ya con el total calculado.
		
		List<OrderPayment> payments = buildPayments(orderCreateDTO.getPayments());
		String paymentTypeLabel = payments.stream()
				.map(OrderPayment::getPaymentMethod).distinct().count() == 1
				? payments.get(0).getPaymentMethod()
						: MIXED_PAYMENT_LABEL;
		
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
		BigDecimal total = subtotal.add(taxes).add(order.getShippingFee()).setScale(2,RoundingMode.HALF_UP);
		BigDecimal paid = payments.stream().map(OrderPayment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
		if(paid.compareTo(total) != 0) {
			throw new InvalidRequestException("Los pagos suman $" + paid.toPlainString()
						+ "pero el total de la factura es $" + total.toPlainString() + ".");
		}
		LocalDateTime paidAt = LocalDateTime.now();
		order.setPaidDate(paidAt);
		
		try {
			Order savedOrder = orderRepository.save(order);
			//Recien aca tenemos el Order con ID, para poder linkear las 
			//transacciones de inventario (customer_order_id) y guardarlas.
			inventoryTransactionRepository.saveAll(stockMovements);
			
			payments.forEach(p -> {
				p.setOrder(savedOrder);
				p.setPaidAt(paidAt);
			});
			List<OrderPayment> savedPayments = orderPaymentRepository.saveAll(payments);
			
			OrderDTO result = orderMapper.toOrderDTO(savedOrder);
			result.setPayments(savedPayments.stream().map(orderPaymentMapper::toDTO).toList());
			return result;
						
		}catch(DataAccessException e) {
			throw new DatabaseException("Error al crear el pedido", e);
		}
		
	}
	
	
	/**
	 * Valida la lista de pagos ( 1 a MAX_PAYMENTS) y arma un OrderPayment por
	 * cada uno, todavia sin pedido ni fecha. Cada monto tiene que ser mayor
	 * a 0 y con a lo sumo 2 decimales. Que la suma de los pagos coincida con
	 * el total se verifica en createOrder, cuando el total ya esta calculado.
	 */
	
	private List<OrderPayment> buildPayments (List<PaymentDetailsDTO> inputs){
		if(inputs == null || inputs.isEmpty()) {
			throw new InvalidRequestException("Indicá una forma de pago.");
		}
		
		if(inputs.size() > MAX_PAYMENTS) {
			throw new InvalidRequestException("Una factura admite hasta " + MAX_PAYMENTS + "pagos.");
		}
		
		List<OrderPayment> payments = new ArrayList<>();
		for (PaymentDetailsDTO in : inputs) {
			PaymentMethod method = PaymentMethod.fromLabel(in.getPaymentMethod())
					.orElseThrow(() -> new InvalidRequestException(
							"Forma de pago no soportada: " + in.getPaymentMethod()));
			OrderPayment payment = buildPayment(method, in);
			payment.setAmount(validAmount(in.getAmount()));
			payments.add(payment);
			
		}
		return payments;
	}
	
	private BigDecimal validAmount(BigDecimal amount) {
		if(amount == null || amount.signum() <= 0) {
			throw new InvalidRequestException("Cada pago debe tener un monto mayor a 0.");
		}
		try {
			return amount.setScale(2, RoundingMode.UNNECESSARY);
		}catch(ArithmeticException e) {
			throw new InvalidRequestException("El monto de cada pago puede tener como máximo 2 decimales.");
		}
	}
	
	/**
	 * Valida los datos de UN pago segun su metodo y arma el OrderPayment
	 * (sin pedido, monto ni fecha). Efectivo no pide datos.
	 */
	
	private OrderPayment buildPayment (PaymentMethod method, PaymentDetailsDTO in) {
		OrderPayment payment = OrderPayment.builder()
				.paymentMethod(method.getLabel())
				.build();
		
		switch(method) {
		case CASH:
			break;
		case CARD:
			if(in == null) {
				throw new InvalidRequestException("Faltan los datos de la tarjeta.");
			}
			String cardType = clean(in.getCardType());
			if (cardType == null
					|| !(cardType.equalsIgnoreCase("DEBIT") || cardType.equalsIgnoreCase("CREDIT"))) {
				throw new InvalidRequestException("Indicá si la tarjeta es de débito o de crédito.");
			}
			cardType = cardType.toUpperCase();
			payment.setCardType(cardType);
			payment.setCardBrand(required(in.getCardBrand(), "la marca de la tarjeta", 30));
			payment.setPosVoucherNumber(required(in.getPosVoucherNumber(), "el número de cupón del posnet.", 30));
			payment.setAuthorizationCode(required(in.getAuthorizationCode(),"el código de autorización", 20));
			
			String last4 = clean(in.getCardLast4());
			if(last4 != null && !last4.matches("\\d{4}")) {
				throw new InvalidRequestException("Los últimos 4 dígitos de la tarjeta deben ser 4 números.");
			}
			payment.setCardLast4(last4);
			
			if(cardType.equals("CREDIT")) {
				Integer installments = in.getInstallments();
				if(installments == null || installments < 1 || installments > 24) {
					throw new InvalidRequestException("Las cuotas deben estar entre 1 y 24.");
				}
				payment.setInstallments(installments);
			}
			break;
		case TRANSFER:
			if(in == null) {
				throw new InvalidRequestException("Faltan los datos de la transferencia.");
			}
			payment.setTransferReference(required(in.getTransferReference(),
					"el número de operación de la transferencia", 50));
			payment.setTransferBank(optional(in.getTransferBank(), 50));
			break;
		}
		return payment;
	}
	
	private String clean(String value) {
		if(value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}
	
	private String required(String value, String fieldName, int maxLength) {
		String cleaned = clean(value);
		if(cleaned == null) {
			throw new InvalidRequestException("Falta" + fieldName + ".");
		}
		if(cleaned.length() > maxLength) {
			throw new InvalidRequestException(
				"Se superó el máximo de "+ maxLength + "caracteres en "+ fieldName + ".");
		}
		return cleaned;
	}
	
	private String optional(String value, int maxLength) {
		String cleaned = clean(value);
		if(cleaned != null && cleaned.length() > maxLength) {
			throw new InvalidRequestException("Se superó el máximo de "+ maxLength + " caracteres.");
		}
		return cleaned;
	}
	//Una sola consulta para todos los pedidos de la pagina (evita n+1)
	private void attachPayments(List<OrderDTO> dtos) {
		if(dtos.isEmpty()) {
			return;
		}
		List<Integer> ids = dtos.stream().map(OrderDTO::getOrderID).toList();
		Map<Integer, List<OrderPayment>> byOrder = orderPaymentRepository.findByOrderIds(ids).stream()
				.collect(Collectors.groupingBy(p->p.getOrder().getId()));
		dtos.forEach(d -> {
			List<OrderPayment> list = byOrder.get(d.getOrderID());
			if(list != null) {
				d.setPayments(list.stream().map(orderPaymentMapper::toDTO).toList());
			}
		});
	}
	
	
	@Override
	public OrderDTO getOrderById(Integer id) {
		OrderDTO dto = orderRepository.findById(id)
				.map(orderMapper::toOrderDTO)
				.orElseThrow(()->new ResourceNotFoundException("Order not foudn"));
		dto.setPayments(orderPaymentRepository.findByOrder_IdOrderByIdAsc(id).stream()
				.map(orderPaymentMapper::toDTO).toList());
		return dto;
				
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













