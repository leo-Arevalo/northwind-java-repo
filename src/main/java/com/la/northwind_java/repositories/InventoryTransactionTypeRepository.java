package com.la.northwind_java.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.la.northwind_java.models.InventoryTransactionType;

public interface InventoryTransactionTypeRepository extends JpaRepository<InventoryTransactionType, Byte>{
	Optional<InventoryTransactionType> findByTypeName(String typeName);

}
