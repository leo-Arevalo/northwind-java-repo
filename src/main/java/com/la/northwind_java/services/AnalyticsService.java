
package com.la.northwind_java.services;

import java.util.List;

import com.la.northwind_java.dtos.AnalyticsKpiDTO;
import com.la.northwind_java.dtos.ChartDataDTO;

public interface AnalyticsService {

	AnalyticsKpiDTO getKpis();
	
	List<ChartDataDTO> getTotalPaidBySupplier();
	
	List<ChartDataDTO> getTotalPaidByCountry();
	
	List<ChartDataDTO> getOrdersByStatus();
	
	List<ChartDataDTO> getMonthlyPayments();
	
	List<ChartDataDTO> getTopSuppliers(int limit);
	
	List<ChartDataDTO> getTopCountries(int limit);
}
