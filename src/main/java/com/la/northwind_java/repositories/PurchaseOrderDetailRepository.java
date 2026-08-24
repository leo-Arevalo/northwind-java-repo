package com.la.northwind_java.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.la.northwind_java.models.PurchaseOrderDetail;

public interface PurchaseOrderDetailRepository extends JpaRepository<PurchaseOrderDetail, Integer>{
	List<PurchaseOrderDetail> findByPurchaseOrder_Id(Integer purchaseOrderId);
}
