
package com.la.northwind_java.repositories;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.la.northwind_java.models.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer>{
	@Query("SELECT o FROM Order o "+
			"LEFT JOIN o.status s "+
			"WHERE (:customerId IS NULL OR o.customer.id = :customerId) "+
			"AND (:employeeId IS NULL OR o.employee.id = :employeeId) "+
			"AND (:status IS NULL OR s.statusName = :status)")
	Page<Order> findOrders(Pageable pageable,
							@Param("customerId") Integer customerId,
							@Param("employeeId") Integer employeeId,
							@Param("status") String status);
	//pageable ya maneja el ordenamiento sortBy y sortDirection
}
