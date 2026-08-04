package com.la.northwind_java.repositories;



import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.la.northwind_java.dtos.ChartDataDTO;
import com.la.northwind_java.models.PurchaseOrder;

public interface AnalyticsRepository extends Repository<PurchaseOrder, Integer> {

	//KPIs
	@Query("SELECT COUNT(s) FROM Supplier s")
	Long countSuppliers();
	
	@Query("SELECT COUNT(p) FROM PurchaseOrder p")
	Long countOrders();
	
	@Query("SELECT COALESCE(SUM(p.paymentAmount), 0) FROM PurchaseOrder p")
	java.math.BigDecimal sumTotalPaid();
	
	//Group by supplier
	@Query("SELECT new com.la.northwind_java.dtos.ChartDataDTO(s.company, COALESCE(SUM(p.paymentAmount),0))"+
			"FROM PurchaseOrder p JOIN p.supplier s " +
			"GROUP BY s.company "+
			"ORDER BY COALESCE(SUM(p.paymentAmount),0) DESC")
	List<ChartDataDTO> totalPaidBySupplier();
	
	//Group by country
	@Query("SELECT new com.la.northwind_java.dtos.ChartDataDTO(s.countryRegion, COALESCE(SUM(p.paymentAmount),0))" +
			"FROM PurchaseOrder p JOIN p.supplier s " +
			"GROUP BY s.countryRegion " +
			"ORDER BY COALESCE(SUM(p.paymentAmount),0) DESC")
	List<ChartDataDTO> totalPaidByCountry();
	
	//Order by status
	@Query("SELECT new com.la.northwind_java.dtos.ChartDataDTO(p.status.status, COUNT(p.id)) " +
			"FROM PurchaseOrder p " +
			"GROUP BY p.status.status " +
			"ORDER BY COUNT(p) DESC")
	List<ChartDataDTO> ordersByStatus();
	
	  // Monthly payments (MySQL specific)
    @Query(value = """
        SELECT DATE_FORMAT(p.creation_date, '%Y-%m') as ym,
               COALESCE(SUM(p.payment_amount),0)
        FROM purchase_orders p
        GROUP BY ym
        ORDER BY ym
        """, nativeQuery = true)
    List<Object[]> paymentsGroupedByMonth();
	
	
	
	
	
	
}






