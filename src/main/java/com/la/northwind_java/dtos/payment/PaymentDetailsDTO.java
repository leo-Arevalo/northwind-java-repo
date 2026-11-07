package com.la.northwind_java.dtos.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Un pago de la factura. Al crear, el front manda paymentMethod, amount y
 * solo los campos del metodo elegido (cardType, cardBrand, cardLast4,
 * installments, posVoucherNumber, authorizationCode, transferReference,
 * transferBank). paidAt lo completa el backend y se devuelve al leer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDetailsDTO {

	private String paymentMethod;
	
	private String cardType;
	private String cardBrand;
	private String cardLast4;
	private Integer installments;
	private String posVoucherNumber;
	private String authorizationCode;
	
	private String transferReference;
	private String transferBank;
	
	private BigDecimal amount;
	private LocalDateTime paidAt;
}
