package com.la.northwind_java.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.la.northwind_java.models.InventoryTransaction;

public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Integer>{
	/**
	 * Calcula el stock disponible de un producto a partir del historial de
	 * transacciones de inventario (no hay una columna "stock" fija en products):
	 * 'Purchased' suma, 'sold'/'Wasted'/'On Hold' restan.
	 *
	 */
	@Query("""
			SELECT COALESCE(SUM(CASE WHEN t.transactionType.typeName = 'Purchased' THEN t.quantity ELSE -t.quantity END), 0)
			FROM InventoryTransaction t
			WHERE t.product.productID = :productId
			""")
	Long getAvailableStock(@Param("productId") Integer productId);
	
}


