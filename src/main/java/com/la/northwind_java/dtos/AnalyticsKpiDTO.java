package com.la.northwind_java.dtos;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsKpiDTO {
	private Long totalSuppliers;
	private Long totalOrders;
	private BigDecimal totalPaid;
}
