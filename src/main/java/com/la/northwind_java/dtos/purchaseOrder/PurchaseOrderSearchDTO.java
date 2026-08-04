
package com.la.northwind_java.dtos.purchaseOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseOrderSearchDTO {

	private Integer supplierId;
	
	private Integer createdById;
	
	private String statusName;
	
	private String paymentMethod;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime fromCreationDate;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime toCreationDate;
	
	private BigDecimal minPayment;
	
	private BigDecimal maxPayment;
	
	
}
