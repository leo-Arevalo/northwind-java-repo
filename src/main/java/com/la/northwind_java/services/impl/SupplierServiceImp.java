package com.la.northwind_java.services.impl;


import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.la.northwind_java.config.exceptions.DatabaseException;
import com.la.northwind_java.config.exceptions.ResourceNotFoundException;
import com.la.northwind_java.dtos.supplier.SupplierCreateDTO;
import com.la.northwind_java.dtos.supplier.SupplierDTO;
import com.la.northwind_java.dtos.supplier.SupplierSearchDTO;
import com.la.northwind_java.dtos.supplier.SupplierUpdateDTO;
import com.la.northwind_java.mappers.SupplierMapper;
import com.la.northwind_java.models.Supplier;
import com.la.northwind_java.repositories.SupplierRepository;
import com.la.northwind_java.services.SupplierService;
import com.la.northwind_java.specification.SupplierSpecification;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class SupplierServiceImp implements SupplierService{

	private final SupplierRepository supplierRepository;
	private final SupplierMapper supplierMapper;
	
	
	/*
	 * Retrieves paginated suppliers with optional filtering and sorting
	 */
	@Override
	@Cacheable(value="suppliers", key="#pageable")
	public Page<SupplierDTO> getSuppliers(SupplierSearchDTO supplierSearchDTO, Pageable pageable) {
		Specification<Supplier> spec = Specification
				.where(SupplierSpecification.hasCompanyLike(supplierSearchDTO.getCompany()))
				.and(SupplierSpecification.liveInCountry(supplierSearchDTO.getCountryRegion()))
				.and(SupplierSpecification.hasName(supplierSearchDTO.getLastName()))
				.and(SupplierSpecification.hasEmail(supplierSearchDTO.getEmailAddress()))
				.and(SupplierSpecification.livesInCity(supplierSearchDTO.getCity()))
				.and(SupplierSpecification.hasOrders(supplierSearchDTO.getHasOrders()))
				.and(SupplierSpecification.hasOrderDateBetween(supplierSearchDTO.getOrderFromDate(), supplierSearchDTO.getOrderToDate()))
				.and(SupplierSpecification.hasTotalPaymentBetween(supplierSearchDTO.getMinTotalPayment(), supplierSearchDTO.getMaxTotalPayment()));
		
		return supplierRepository.findAll(spec, pageable)
				.map(supplierMapper::toSupplierDTO); //devolvemos un SupplierDTO, no un SupplierSearchDTO
		

	}
	
	@Override
	public List<SupplierDTO> findRecentSuppliers(){
		List<Supplier> suppliers = supplierRepository.findRecentSuppliers(PageRequest.of(0, 9));
		return supplierMapper.toDtoList(suppliers);
	}
	
	
	/*
	 * Create a new supplier
	 */
	@Override
	@Transactional
	public SupplierDTO createSupplier(SupplierCreateDTO supplierCreateDTO) {
		try {
			Supplier supplier = supplierMapper.toSupplier(supplierCreateDTO);
			Supplier saved = supplierRepository.save(supplier);
			return supplierMapper.toSupplierDTO(saved);
		}catch(DataAccessException e) {
			throw new DatabaseException("Error creating customer.", e);
		}
	}

	/*
	 * Retrieves a supplier by ID.
	 */
	@Override
	public SupplierDTO getSupplierById(Integer id) {
		return supplierRepository.findById(id)
				.map(supplierMapper::toSupplierDTO)
				.orElseThrow(()-> new ResourceNotFoundException("Supplier not found with id "+id));
	}

	

	/*
	 * Updates an existing supplier.
	 */
	@Override
	@Transactional
	public SupplierDTO updateSupplier(Integer id, SupplierUpdateDTO supplierUpdateDTO) {
		Supplier supplier = supplierRepository.findById(id)
				.orElseThrow(()->new ResourceNotFoundException("Supplier not found with id "+id));
		try {
			supplierMapper.updateSupplierFromDTO(supplierUpdateDTO, supplier);
			Supplier updateSupplier = supplierRepository.save(supplier);
			return supplierMapper.toSupplierDTO(updateSupplier);
		}catch(DataAccessException e) {
			throw new DatabaseException("Error updating supplier with id "+id, e);
		}
	}

	/*
	 * Deletes a supplier.
	 */
	@Override
	@Transactional
	public void deleteSupplier(Integer id) {
		if(!supplierRepository.existsById(id)) {
			throw new ResourceNotFoundException("Supplier not found with id: "+id);
		}
		try {
			supplierRepository.deleteById(id);			
		}catch(Exception e) {
			throw new DatabaseException("Error deleting supplier", e);
		}

	}
	
	
	@Override
	public List<Supplier> searchSuppliers(SupplierSearchDTO filters){
		Specification<Supplier> spec = Specification.where(SupplierSpecification.hasCompanyLike(filters.getCompany()))
				.and(SupplierSpecification.hasName(filters.getFirstName()))
				.and(SupplierSpecification.hasEmail(filters.getEmailAddress()))
				.and(SupplierSpecification.livesInCity(filters.getCity()))
				.and(SupplierSpecification.liveInCountry(filters.getCountryRegion()))
				.and(SupplierSpecification.hasOrders(filters.getHasOrders()))
				.and(SupplierSpecification.hasOrderDateBetween(filters.getOrderFromDate(), filters.getOrderToDate()))
				.and(SupplierSpecification.hasOrderWithMinPayment(filters.getMinTotalPayment()));
		return supplierRepository.findAll(spec);
	}
	
		
	
	
}















