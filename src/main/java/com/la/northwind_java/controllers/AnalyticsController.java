
package com.la.northwind_java.controllers;

import com.la.northwind_java.dtos.AnalyticsKpiDTO;
import com.la.northwind_java.dtos.ChartDataDTO;
import com.la.northwind_java.services.AnalyticsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("/api/dashboard")
//@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Dashboard metrics and aggregated reports for suppliers and purchase orders.")
public class AnalyticsController {
	
	private final AnalyticsService analyticsService;
	
	  public AnalyticsController(AnalyticsService analyticsService) {
	        this.analyticsService = analyticsService;
	    }
	
	@Operation(summary = "Dashboard KPIs",
			description = "Get key metrics like total suppliers, total orders, and total paid.")
	@ApiResponse(responseCode = "200", description = "KPIs retrieved successfully.")
	@GetMapping("/kpis")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_EMPLOYEE')")
	public ResponseEntity<AnalyticsKpiDTO> getKpis(){
		return ResponseEntity.ok(analyticsService.getKpis());
	}
	

    @GetMapping("/suppliers/total-paid")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_EMPLOYEE')")
    @Operation(summary = "Total paid by supplier",
               description = "Aggregated payments grouped by supplier.")
    public ResponseEntity<List<ChartDataDTO>> getTotalPaidBySupplier() {
        return ResponseEntity.ok(analyticsService.getTotalPaidBySupplier());
    }

    @GetMapping("/countries/total-paid")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_EMPLOYEE')")
    @Operation(summary = "Total paid by country",
               description = "Aggregated payments grouped by supplier country.")
    public ResponseEntity<List<ChartDataDTO>> getTotalPaidByCountry() {
        return ResponseEntity.ok(analyticsService.getTotalPaidByCountry());
    }

    @GetMapping("/orders/status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_EMPLOYEE')")
    @Operation(summary = "Orders by status",
               description = "Number of purchase orders grouped by their status.")
    public ResponseEntity<List<ChartDataDTO>> getOrdersByStatus() {
        return ResponseEntity.ok(analyticsService.getOrdersByStatus());
    }

    @GetMapping("/payments/monthly")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_EMPLOYEE')")
    @Operation(summary = "Monthly payments",
               description = "Total payments aggregated per month (YYYY-MM).")
    public ResponseEntity<List<ChartDataDTO>> getMonthlyPayments() {
        return ResponseEntity.ok(analyticsService.getMonthlyPayments());
    }

    @GetMapping("/suppliers/top")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_EMPLOYEE')")
    @Operation(summary = "Top suppliers",
               description = "Top N suppliers ranked by total payments.")
    public ResponseEntity<List<ChartDataDTO>> getTopSuppliers(
            @RequestParam(defaultValue = "5") @Min(1) int limit) {
        return ResponseEntity.ok(analyticsService.getTopSuppliers(limit));
    }

    @GetMapping("/countries/top")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_EMPLOYEE')")
    @Operation(summary = "Top countries",
               description = "Top N countries ranked by total payments.")
    public ResponseEntity<List<ChartDataDTO>> getTopCountries(
            @RequestParam(defaultValue = "5") @Min(1) int limit) {
        return ResponseEntity.ok(analyticsService.getTopCountries(limit));
    }
	
	
	
	
	
}





