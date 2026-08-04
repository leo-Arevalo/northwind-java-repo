package com.la.northwind_java.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.la.northwind_java.models.Customer;


public interface CustomerRepository extends JpaRepository<Customer, Long>{
	
	//@Query("SELECT DISTINCT c FROM Customer c " +
	//		"LEFT JOIN c.orders o " +
	//		"WHERE (:orderId IS NULL OR o.orderID = :orderId)")
	//Page<Customer> findCustomers(Pageable pageable,
	//						@Param("orderId") Integer orderId);
	//DISTINCT porque un cliente puede tener varias ordenes y no queremos duplicados.
	//pageable ya maneja el ordenamiento sortBy y sortDirection

}
