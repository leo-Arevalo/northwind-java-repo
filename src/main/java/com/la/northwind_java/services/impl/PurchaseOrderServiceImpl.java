package com.la.northwind_java.services.impl;

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
import com.la.northwind_java.models.PurchaseOrder;
import com.la.northwind_java.services.*;
import com.la.northwind_java.specification.PurchaseOrderSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PurchaseOrderServiceImpl implements PurchaseOrderService {
	
	private final PurchaseOrderRepository repository;
	private final PurchaseOrderMapper mapper;
	
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
				return mapper.toDTO(repository.save(entity));
		}catch(DataAccessException e) {
			throw new DatabaseException("Error updating purchase order with id " +id, e);
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
