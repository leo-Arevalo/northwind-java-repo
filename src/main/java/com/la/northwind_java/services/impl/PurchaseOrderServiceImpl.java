package com.la.northwind_java.services.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.la.northwind_java.repositories.*;

import jakarta.transaction.Transactional;

import com.la.northwind_java.config.exceptions.DatabaseException;
import com.la.northwind_java.config.exceptions.ResourceNotFoundException;
import com.la.northwind_java.dtos.purchaseOrder.*;
import com.la.northwind_java.mappers.PurchaseOrderMapper;
import com.la.northwind_java.models.Product;
import com.la.northwind_java.models.Employee;
import com.la.northwind_java.models.InventoryTransaction;
import com.la.northwind_java.models.InventoryTransactionType;
import com.la.northwind_java.models.Product;
import com.la.northwind_java.models.PurchaseOrder;
import com.la.northwind_java.models.PurchaseOrderDetail;
import com.la.northwind_java.models.PurchaseOrderStatus;
import com.la.northwind_java.models.Supplier;
import com.la.northwind_java.services.*;
import com.la.northwind_java.models.Supplier;
import com.la.northwind_java.specification.PurchaseOrderSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PurchaseOrderServiceImpl implements PurchaseOrderService {
	
	private final SupplierRepository supplierRepository;
	
	private final PurchaseOrderRepository repository;
	private final PurchaseOrderMapper mapper;
	private final PurchaseOrderStatusRepository statusRepository;
	private final PurchaseOrderDetailRepository detailRepository;
	private final InventoryTransactionRepository inventoryTransactionRepository;
	private final InventoryTransactionTypeRepository inventoryTransactionTypeRepository;
	private final ProductRepository productRepository;
	private final EmployeeRepository employeeRepository;
	
	//Estado por defecto para una orden de compra recien creada
	//purchase_order_status: 0 = 'NEW'
	private static final Integer DEFAULT_PO_STATUS_ID = 0;
	
	@Override
	public Page<PurchaseOrderDTO> getOrders(PurchaseOrderSearchDTO filters, Pageable pageable){
		return repository.findAll(PurchaseOrderSpecification.withFilters(filters), pageable).
				map(mapper::toDTO);
	}
	
	@Override
	public PurchaseOrderDTO getById(Integer id) {
		
		PurchaseOrder po = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder not found with id: "+id));
		PurchaseOrderDTO dto = mapper.toDTO(po);
		dto.setOrderDetails(detailRepository.findByPurchaseOrder_Id(id).stream()
				.map(line -> PurchaseOrderLineDTO.builder()
						.productId(line.getProduct().getProductID())
						.productName(line.getProduct().getProductName())
						.quantity(line.getQuantity())
						.unitCost(line.getUnitCost())
						.postedToInventory(line.getPostedToInventory())
						.build())
				.toList());
		return dto;
		
	}
	
	@Override
	public List<PurchaseOrderDTO> findRecentOrders(){
		return mapper.toDtoList(repository.findTop10ByOrderByCreationDateDesc());
		
	}
	@Override
	public List<PurchaseOrderDTO> getBySupplierId(Integer supplierId){
		return mapper.toDtoList(repository.findBySupplierId(supplierId));
	}
	
	@Override
	@Transactional
	public PurchaseOrderDTO create(PurchaseOrderCreateDTO dto) {
		try {
			
			Supplier supplier = supplierRepository.findById(dto.getSupplierId())
					.orElseThrow(()-> new ResourceNotFoundException(
							"Proveedor no encontrado con id: "+dto.getSupplierId()));
			
			PurchaseOrderStatus status = statusRepository.findById(
					dto.getStatusId() != null ? dto.getStatusId() : DEFAULT_PO_STATUS_ID)
					.orElseThrow(()-> new ResourceNotFoundException("Estado de orden de compra no encontrado."));
			
			/*
			 * createdBy es obligatorio (NOT NULL) en la base, pero no
			 * tiene sentido que el frontend lo mande
			 * se resuelve del empleado vinculado al usuario logueado.
			 */
			Employee createdByEmployee = resolveCreatedBy(dto.getCreatedBy());
			
			PurchaseOrder entity = mapper.toEntity(dto);
			
			/* el mapper ignora estas relaciones a proposito
			 * (no puede resolverlas solo con un id suelto)
			 * se resuelve aca.
			 */
			entity.setSupplier(supplier);
			entity.setStatus(status);
			entity.setCreatedBy(createdByEmployee);
			entity.setCreationDate(dto.getCreationDate() != null ? dto.getCreationDate().atStartOfDay(): LocalDateTime.now());
			entity.setShippingFee(dto.getShippingFee() != null ? dto.getShippingFee() : java.math.BigDecimal.ZERO);
			entity.setTaxes(dto.getTaxes() != null ? dto.getTaxes() : java.math.BigDecimal.ZERO);
			entity.setExpectedDate(dto.getExpectedDate() != null ? dto.getExpectedDate().atStartOfDay() : null);
			PurchaseOrder saved = repository.save(entity);
			
			List<PurchaseOrderDetail> lines = dto.getOrderDetails().stream()
					.map(line-> {
						Product product = productRepository.findById(line.getProductId())
								.orElseThrow(()-> new ResourceNotFoundException(
										"Producto no encontrado con id: "+ line.getProductId()));
						return PurchaseOrderDetail.builder()
								.purchaseOrder(saved)
								.product(product)
								.quantity(line.getQuantity())
								.unitCost(line.getUnitCost())
								.postedToInventory(false)
								.build();
					})
					.toList();
			detailRepository.saveAll(lines);
			
			PurchaseOrderDTO result = mapper.toDTO(saved);
			result.setOrderDetails(lines.stream()
					.map(l -> PurchaseOrderLineDTO.builder()
							.productId(l.getProduct().getProductID())
							.productName(l.getProduct().getProductName())
							.quantity(l.getQuantity())
							.unitCost(l.getUnitCost())
							.postedToInventory(l.getPostedToInventory())
							.build())
					.toList());
			return result;

		}catch(DataAccessException e) {
			throw new DatabaseException("Error creating purchase order.", e);
		}
	}
	
	@Override
	@Transactional
	public PurchaseOrderDTO update(Integer id, PurchaseOrderUpdateDTO dto) {
		PurchaseOrder entity = repository.findById(id)
				.orElseThrow(()->new ResourceNotFoundException("PurchaseOrder not found with id "+ id));
		try {
				mapper.updateEntity(dto, entity);
				
				//El mapper ignora "status" a proposito (statusId es un id suelto,
				// no puede resolver la relacion solo). Se resuelve aca.
				
				boolean wasAlreadyClosed = entity.getStatus() != null
						&& "Closed".equalsIgnoreCase(entity.getStatus().getStatus());
				
				if(dto.getStatusId() != null) {
					PurchaseOrderStatus status = statusRepository.findById(dto.getStatusId())
							.orElseThrow(() -> new ResourceNotFoundException(
									"Estado de orden de compra no encontrado: "+ dto.getStatusId()));
					entity.setStatus(status);
				}
				PurchaseOrder saved = repository.save(entity);
				
				boolean isNowClosed = saved.getStatus() != null
						&& "Closed".equalsIgnoreCase(saved.getStatus().getStatus());
				
				//Al cerrar (recibir) la orden, se postean al inventario las
				//lineas que tovia no se postearon - asi se repone stock.
				
				if(isNowClosed && !wasAlreadyClosed) {
					requirePrivilege("PRIV_PURCHASE_APPROVALS");
					postPendingLinesToInventory(saved);
				}
				
				return mapper.toDTO(saved);
		}catch(DataAccessException e) {
			throw new DatabaseException("Error updating purchase order with id " +id, e);
		}
	}
	

	/**
	 * Resuelve quien crea la orden de compra. Si el frontend mando un
	 * id explicito, se respeta (ej. un admin cargando un nombre de otro
	 * empleado). Si no, se toma el Employee cinvulado al usuario logueado
	 * - y si eses usuario no tiene ningun Employee vinculado, se corta
	 * con un mensaje claro en vez de un NOT NULL de la base.
	 *
	 */
	private Employee resolveCreatedBy (Integer explicitEmployeeId) {
		if(explicitEmployeeId != null) {
			return employeeRepositoryOrThrow(explicitEmployeeId);
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(auth != null && auth.getPrincipal() instanceof com.la.northwind_java.security.models.User user
				&& user.getEmployee() != null) {
			return user.getEmployee();
		}
		throw new com.la.northwind_java.config.exceptions.InvalidRequestException(
				"Tu usuario no tiene un empleado vinculado, no se puede registrar quien crea la orden. "
				+"Pedile a un administrador que te vincule a un Employee (PATCH/api/users/{id}/employee.)");
	}
	private Employee employeeRepositoryOrThrow(Integer id) {
		return employeeRepository.findById(id)
				.orElseThrow(()-> new ResourceNotFoundException("Empleado no encontrado con id: "+ id));
		}
	/**
	 * Chequeo de autorizacion a nivel de regla de negocio, no de endpoint:
	 * cualquier ROLE_ADMIN puede editar campos sueltos de una orden de
	 * compra (@PreAuthorize del controller), pero aprobarla/cerrarla -
	 * la accion que dispara movimientos reales de inventario - requiere
	 * ademas el privilegio especifico del Employee vinculado al usuario.
	 */
	private void requirePrivilege(String authority) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		boolean hasPrivilege = auth != null && auth.getAuthorities().stream()
				.anyMatch(a -> a.getAuthority().equals(authority));
		if(!hasPrivilege) {
			throw new AccessDeniedException(
					"Se requiere el privilegio 'Purchase Approvals' para aprobar/cerrar una orden de compra.");
		}
	}
	
	
	
	private void postPendingLinesToInventory(PurchaseOrder order) {
		InventoryTransactionType purchasedType = inventoryTransactionTypeRepository.findByTypeName("Purchased")
				.orElseThrow(() -> new ResourceNotFoundException (
					"No se encontro el tipo de transacción de inventario 'Purchased'. Verificar seed de la base."));
		List<PurchaseOrderDetail> lines = detailRepository.findByPurchaseOrder_Id(order.getId());
		for(PurchaseOrderDetail line : lines) {
			if(Boolean.TRUE.equals(line.getPostedToInventory())) {
				continue; //ya se poseo antes, no duplicar
			}
			
			InventoryTransaction movement = InventoryTransaction.builder()
					.transactionType(purchasedType)
					.product(line.getProduct())
					.quantity(line.getQuantity() != null ? line.getQuantity().intValue() : 0)
					.purchaseOrder(order)
					.transactionModifiedDate(LocalDateTime.now())
					.comments("Recepcion de compra #" + order.getId())
					.build();
			InventoryTransaction savedMovement = inventoryTransactionRepository.save(movement);
			
			line.setInventoryTransaction(savedMovement);
			line.setPostedToInventory(true);
			line.setDateReceived(LocalDateTime.now());
			detailRepository.save(line);	
		}
	}
	
	@Override
	@Transactional
	public void delete(Integer id) {
		if(!repository.existsById(id)) {
			throw new ResourceNotFoundException("PurchaseOrder not found with id: "+id);
		}
		try {
			repository.deleteById(id);
		}catch(Exception e) {
			throw new DatabaseException("Error deleting purchase order", e);
		}
	}
	
	@Override
	public List<PurchaseOrderDTO> getPendingOrders(){
		return mapper.toDtoList(repository.findByStatus_StatusIgnoreCase("PENDING"));
	}
	@Override
	public Double getTotalPaidToSupplier(Integer supplierId) {
		return repository.sumPaymentAmountBySupplierId(supplierId).orElse(0.0);
	}
	
	@Override
	public List<PurchaseOrderDTO> getByStatus(String status){
		return mapper.toDtoList(repository.findByStatus_StatusIgnoreCase(status));
	}
	

}
