

package com.la.northwind_java.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * DTO for creating an Order.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreateDTO {
	
	
	private Integer customerId;
	private Integer employeeId;
	@PastOrPresent
	private LocalDateTime orderDate;
	private Integer shipperId;
	@NotBlank
	@Size(max = 50)
	private String shipName;
	private String shipAddress;
	private String shipCity;
	private String shipStateProvince;
	private String shipPostalCode;
	private String shipCountryRegion;
	@DecimalMin("0.00")
	private BigDecimal shippingFee;
	@DecimalMin("0.00")
	private BigDecimal taxes;
	@NotBlank
	private String paymentType;
	private LocalDateTime paidDate;
	private String notes;
	private Double taxRate;
	private Integer taxStatusId;
	private Integer statusId;
	private List<OrderDetailsDTO> orderDetails;
	
	
}
