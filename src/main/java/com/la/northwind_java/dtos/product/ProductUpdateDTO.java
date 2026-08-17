package com.la.northwind_java.dtos.product;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ProductUpdateDTO {

	@NotNull(message = "Product ID is required")
	private Integer productID;
	
	@Size(max = 25, message = "Product code must not exceed 25 characters.")
	private String productCode;
	
	@NotBlank(message = "Product name is required")
	@Size(max = 100, message = "Product name must not exceed 100 characters")
	private String productName;
	
	private String description;
	
	@NotNull(message = "Standard cost is required")
	private BigDecimal standardCost;
	
	@NotNull(message = "List price is required")
	private BigDecimal listPrice;
	
	private Integer reorderLevel;
	
	private Integer targetLevel;
	
	@Size(max = 50, message = "Quantity per unit must not exceed 50 characters")
	private String quantityPerUnit;
	
	@NotNull(message = "Discontinued status is required")
	private Boolean discontinued = false;
	
	private Integer minimumReorderQuantity;
	
	@Size(max = 50, message = "Category must not exceed 50 characters")
	private String category;
}
