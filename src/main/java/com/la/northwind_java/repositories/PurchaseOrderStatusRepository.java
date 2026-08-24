package com.la.northwind_java.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.la.northwind_java.models.PurchaseOrderStatus;

public interface PurchaseOrderStatusRepository extends JpaRepository<PurchaseOrderStatus, Integer>{

}
