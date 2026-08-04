package com.la.northwind_java.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.la.northwind_java.models.Supplier;

public interface SupplierRepository extends JpaRepository<Supplier, Integer>, JpaSpecificationExecutor<Supplier>{

	//@Query("SELECT o FROM Supplier s"+
	//		"LEFT JOIN s." completar
	//		)
	//Page<Supplier> findSuppliers(Pageable pageable, completar);
	@Query("SELECT DISTINCT s FROM Supplier s JOIN s.purchaseOrders po ORDER BY po.creationDate DESC")
	List<Supplier> findRecentSuppliers(Pageable pageable);
	
	
	
	
	
}
