package com.la.northwind_java.services.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.la.northwind_java.dtos.AnalyticsKpiDTO;
import com.la.northwind_java.dtos.ChartDataDTO;
import com.la.northwind_java.repositories.AnalyticsRepository;
import com.la.northwind_java.services.AnalyticsService;

import lombok.RequiredArgsConstructor;


@Service
//@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService{

	private final AnalyticsRepository repo;
	
	public AnalyticsServiceImpl(AnalyticsRepository repo) {
        this.repo = repo;
    }
	
	@Override
	@Transactional(readOnly = true)
	public AnalyticsKpiDTO getKpis() {
		return new AnalyticsKpiDTO(
				repo.countSuppliers(),
				repo.countOrders(),
				repo.sumTotalPaid() != null ? repo.sumTotalPaid() : BigDecimal.ZERO
				);
	}

	@Override
	public List<ChartDataDTO> getTotalPaidBySupplier() {
		return repo.totalPaidBySupplier();
	}

	@Override
	public List<ChartDataDTO> getTotalPaidByCountry() {
		return repo.totalPaidByCountry();
	}

	@Override
	public List<ChartDataDTO> getOrdersByStatus() {
		return repo.ordersByStatus();
	}

	@Override
	public List<ChartDataDTO> getMonthlyPayments() {
		List<Object[]> rows = repo.paymentsGroupedByMonth();
		List<ChartDataDTO> result = new ArrayList<>();
		for(Object[] row : rows) {
			result.add(new ChartDataDTO(
					row[0] != null ? row[0].toString() : "N/A",
					row[1] != null ? new BigDecimal(row[1].toString()):BigDecimal.ZERO
					));
		}
		return result;
	}

	@Override
	public List<ChartDataDTO> getTopSuppliers(int limit) {
		return repo.totalPaidBySupplier().stream().limit(limit).toList();
	}

	@Override
	public List<ChartDataDTO> getTopCountries(int limit) {
		return repo.totalPaidByCountry().stream().limit(limit).toList();
	}

}
