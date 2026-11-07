package com.la.northwind_java.repositories;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.la.northwind_java.models.OrderPayment;

public interface OrderPaymentRepository extends JpaRepository<OrderPayment, Integer>{
	List<OrderPayment> findByOrder_IdOrderByIdAsc(Integer orderId);
	
	//Para traer los pagos de toda una pagina de pedidos con una sola consulta
	@Query("Select p from OrderPayment p join fetch p.order o where o.id in :orderIds order by p.id")
	List<OrderPayment> findByOrderIds(@Param("orderIds") Collection<Integer> orderIds);
	
}
