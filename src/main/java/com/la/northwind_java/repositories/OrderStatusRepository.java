package com.la.northwind_java.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.la.northwind_java.models.InventoryTransactionType;
import com.la.northwind_java.models.OrderStatus;

public interface OrderStatusRepository extends JpaRepository<OrderStatus, Byte> {
	Optional<InventoryTransactionType> findByTypeName(String typeName);
}
