
package com.la.northwind_java.dtos.supplier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springdoc.core.annotations.ParameterObject;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@ParameterObject
@Schema(description = "DTO to filter supplier and their associated purchase orders.")
public class SupplierSearchDTO {
	
	@Schema(description = "Company name (partial or full match), example = ACME")
	@Size(max = 100, message = "Company name must be at most 100 characters.") 
	private String company;
	
	@Schema(description = "First name of the supplier", example = "John")
	@Size(max = 50, message = "First name must be at most 50 characters.")
	private String firstName;
	
	@Schema(description = "Last name of the supplier", example = "Doe")
	@Size(max = 50, message = "Last name must be at most 50 characters.")
	private String lastName;
	
	@Schema(description = "Email address of the supplier", example = "john.doe@example.com")
	@Email(message = "Invalid email format")
	private String emailAddress;
	
	@Schema(description = "City where the supplier is located", example = "New York")
	@Size(max = 100)
	private String city;
	
	@Schema(description = "Country or region of the supplier", example = "USA")
	@Size(max = 100)
	private String countryRegion;
	
	//Filtros de purchaseOrders opcionales
	
	@Schema(description = "Whether the supplier has purchase orders", example = "true")
	private Boolean hasOrders;
	
	@Schema(description = "Minimum order date", example = "2023-01-01T00:00:00")
	private LocalDateTime orderFromDate;
	
	@Schema(description = "Maximum order date", example = "2023-12-21T23:59:59")
	private LocalDateTime orderToDate;
	
	@Schema(description = "Minimum total payment for purchase orders", example = "100.00")
	@DecimalMin(value = "0.0", inclusive = true, message = "Minimum payment must be non-negative")
	private BigDecimal minTotalPayment;
	@Schema(description = "Maximum total payment for purchase orders", example = "1000.00")
	@DecimalMin(value ="0.0", inclusive = true, message = "Maximum payment must be non-negative")
	private BigDecimal maxTotalPayment;
}








