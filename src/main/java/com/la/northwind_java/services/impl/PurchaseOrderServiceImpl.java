package com.la.northwind_java.services.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.la.northwind_java.repositories.*;

import jakarta.transaction.Transactional;

import com.la.northwind_java.config.exceptions.DatabaseException;
import com.la.northwind_java.config.exceptions.ResourceNotFoundException;
import com.la.northwind_java.dtos.purchaseOrder.*;
import com.la.northwind_java.mappers.PurchaseOrderMapper;
import com.la.northwind_java.models.InventoryTransaction;
import com.la.northwind_java.models.InventoryTransactionType;
import com.la.northwind_java.models.PurchaseOrder;
import com.la.northwind_java.models.PurchaseOrderDetail;
import com.la.northwind_java.models.PurchaseOrderStatus;
import com.la.northwind_java.services.*;
import com.la.northwind_java.specification.PurchaseOrderSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PurchaseOrderServiceImpl implements PurchaseOrderService {
	
	private final PurchaseOrderRepository repository;
	private final PurchaseOrderMapper mapper;
	private final PurchaseOrderStatusRepository statusRepository;
	private final PurchaseOrderDetailRepository detailRepository;
	private final InventoryTransactionRepository inventoryTransactionRepository;
	private final InventoryTransactionTypeRepository inventoryTransactionTypeRepository;
	
	@Override
	public Page<PurchaseOrderDTO> getOrders(PurchaseOrderSearchDTO filters, Pageable pageable){
		return repository.findAll(PurchaseOrderSpecification.withFilters(filters), pageable).
				map(mapper::toDTO);
	}
	
	@Override
	public PurchaseOrderDTO getById(Integer id) {
		return repository.findById(id)
				.map(mapper::toDTO)
				.orElseThrow(()-> new ResourceNotFoundException("PurchaseOrder not found with id "+id));
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
			PurchaseOrder entity = mapper.toEntity(dto);
			PurchaseOrder saved = repository.save(entity);
			return mapper.toDTO(saved);
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
					postPendingLinesToInventory(saved);
				}
				
				return mapper.toDTO(saved);
		}catch(DataAccessException e) {
			throw new DatabaseException("Error updating purchase order with id " +id, e);
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
