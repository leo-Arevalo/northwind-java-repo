package com.la.northwind_java.dtos.purchaseOrder;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating existing purchase orders
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseOrderUpdateDTO {

	@NotNull(message = "Id is required.")
	private Integer id;
	
	@NotNull(message = "Supplier ID is required.")
	@Positive(message = "Supplier ID must be a positive number.")
	private Integer supplierId;
	
	@NotNull(message = "CreatedBy is required.")
	@Positive(message = "CreatedBy must be a positive number.")
	private Integer createdBy;
	
	private LocalDate submittedDate;
	
	@NotNull(message = "Created Date is required")
	private LocalDate creationDate;
	
	@NotNull(message = "Status ID is required")
	@Positive(message = "Status ID must be a positive number.")
	private Integer statusId;
	
	private LocalDate expectedDate;
	
	@NotNull(message = "Shipping fee is required")
	@DecimalMin(value = "0.0", inclusive = true, message = "Shipping fee must be non-negative")
	private BigDecimal shippingFee;
	
	@NotNull(message = "Taxes value is required")
	@DecimalMin(value = "0.0", inclusive = true, message = "Taxes must be non-negative")
	private BigDecimal taxes;
	
	
	
	private LocalDate paymentDate;
	
	@DecimalMin(value = "0.0", inclusive = true, message = "Payment Amount must be non-negative")
	private BigDecimal paymentAmount;
	
	@Size(max = 50, message = "Payment Method must not exceed 50 characters.")
	private String paymentMethod;
		
	@Size(max = 1000, message = "Notes must not exceed 1000 characters.")
	private String notes;
	
	@Positive(message = "ApprovedBy must be a positive number.")
	private Integer approvedBy;
	
	private LocalDate approvedDate;
	@Positive(message = "SubmittedBy must be a positive number.")
	private Integer submittedBy;
	
}
