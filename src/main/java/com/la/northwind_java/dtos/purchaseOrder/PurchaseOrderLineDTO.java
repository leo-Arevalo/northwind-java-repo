package com.la.northwind_java.dtos.purchaseOrder;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderLineDTO {

	@NotNull(message = "El producto es obligatorio.")
	private Integer productId;

	//Solo lectura: se completa al mapear la respuesta, se ignora al crear.
	private String productName;
	
	@NotNull(message = "La cantidad es obligatoria")
	@Positive(message = "La cantidad debe ser mayor a 0")
	private BigDecimal quantity;
	
	@NotNull(message = "El costo unitario es obligatorio.")
	@Positive(message = "El costo unitario debe ser mayor a 0")
	private BigDecimal unitCost;
	
	//Solo lectura, viene del backend
	private Boolean postedToInventory;

}
