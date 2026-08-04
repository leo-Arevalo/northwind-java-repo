package com.la.northwind_java.services;




import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.la.northwind_java.dtos.customer.CustomerCreateDTO;
import com.la.northwind_java.dtos.customer.CustomerDTO;
import com.la.northwind_java.dtos.customer.CustomerUpdateDTO;


public interface CustomerService {
	CustomerDTO createCustomer(CustomerCreateDTO customerCreateDTO);
	CustomerDTO getCustomerById(Long id);
	Page<CustomerDTO> getAllCustomers(Pageable pageable);
	CustomerDTO updateCustomer(Long id, CustomerUpdateDTO customerUpdateDTO);
	void deleteCustomer(Long id);
}
