package com.la.northwind_java.services.impl;


import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.la.northwind_java.config.exceptions.DatabaseException;
import com.la.northwind_java.config.exceptions.ResourceNotFoundException;
import com.la.northwind_java.dtos.customer.CustomerCreateDTO;
import com.la.northwind_java.dtos.customer.CustomerDTO;
import com.la.northwind_java.dtos.customer.CustomerUpdateDTO;
import com.la.northwind_java.mappers.CustomerMapper;
import com.la.northwind_java.models.Customer;
import com.la.northwind_java.repositories.CustomerRepository;
import com.la.northwind_java.services.CustomerService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;






@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService{

	private final CustomerRepository customerRepository;
	
	private final CustomerMapper customerMapper;
	
//	@Autowired
//	public CustomerServiceImpl(CustomerRepository customerRepository, CustomerMapper customerMapper) {
//		this.customerRepository = customerRepository;
//		this.customerMapper = customerMapper;
//	}
	
	/*
	 * Creates a new customer.
	 */
	@Override
	public CustomerDTO createCustomer(CustomerCreateDTO customerCreateDTO) {
		try {
			Customer customer = customerMapper.toCustomer(customerCreateDTO);
			customer = customerRepository.save(customer);
			return customerMapper.toCustomerDTO(customer);			
		}catch(DataAccessException e) {
			throw new DatabaseException("Error creating customer", e);
		}

	}

	/*
	 * Retrieves a customer by ID.
	 */
	@Override
	public CustomerDTO getCustomerById(Long id) {
		return customerRepository.findById(id)
				.map(customerMapper::toCustomerDTO)
				.orElseThrow(()-> new ResourceNotFoundException("Customer not found with id"+id));
	}

	/*
	 * Retrieves paginated customers with optional filtering and sorting.
	 */
	
	
	@Override
	@Cacheable(value = "customers", key = "#pageable")
	public Page<CustomerDTO> getAllCustomers(Pageable pageable){
		Page<Customer> customers = customerRepository.findAll(pageable);
		return customers.map(customerMapper::toCustomerDTO);
	}
	
	/*
	@Override
	@Cacheable(value = "customers", key="#page + '-' +#size + '-' +#sortBy + '-' + #sortDirection")
	public Page<CustomerDTO> getAllCustomers(int page, int size, String sortBy, String sortDirection) {
		Sort sort = sortDirection.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
		Pageable pageable = PageRequest.of(page, size, sort);
		Page<Customer> customers = customerRepository.findAll(pageable);
		return customers.map(customerMapper::toCustomerDTO);
	}
	*/
	
	/*
	 * Updates an existing customer.
	 */
	@Override
	@Transactional
	public CustomerDTO updateCustomer(Long id, CustomerUpdateDTO customerUpdateDTO) {
		Customer customer = customerRepository.findById(id)
					.orElseThrow(()-> new ResourceNotFoundException("Customer not found with id" + id));
		try {
			customerMapper.updateEntity(customerUpdateDTO, customer);
			Customer updatedCustomer = customerRepository.save(customer);
			return customerMapper.toCustomerDTO(updatedCustomer);
		}catch(DataAccessException e) {
			throw new DatabaseException("Error updating customer with id" + id, e);
		}
		
	}

	/**
	 * Deletes a customer.
	 */
	@Override
	@Transactional
	public void deleteCustomer(Long id) {
		if(!customerRepository.existsById(id)) {
			throw new ResourceNotFoundException("Customer not found with id: " + id);
		}
		customerRepository.deleteById(id);
	}

}
