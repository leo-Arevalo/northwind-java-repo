
package com.la.northwind_java.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.la.northwind_java.models.PurchaseOrder;


public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Integer>, JpaSpecificationExecutor<PurchaseOrder>{

	List<PurchaseOrder> findTop10ByOrderByCreationDateDesc();
	
	List<PurchaseOrder> findBySupplierId(Integer supplierId);
	
	List<PurchaseOrder> findByStatus_StatusIgnoreCase(String statusName);
	@Query("SELECT SUM(p.paymentAmount) FROM PurchaseOrder p WHERE p.supplier.id = :supplierId")
	Optional<Double> sumPaymentAmountBySupplierId(Integer supplierId);
}
