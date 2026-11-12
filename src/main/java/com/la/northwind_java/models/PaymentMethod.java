package com.la.northwind_java.models;

import java.util.Optional;
import java.util.Arrays;

/**
 * Formas de pago soportadas. El label es lo que se guarda en
 * orders.payment_type y order_payments.payment_method, y lo
 * que manda el selector de la pantalla de facturación.
 */
public enum PaymentMethod {

	CASH("Efectivo"),
	CARD("Tarjeta"),
	TRANSFER("Transferencia");
	
	private final String label;
	
	PaymentMethod(String label){
		this.label= label;
	}
	public String getLabel() {
		return label;
	}
	
	public static Optional<PaymentMethod> fromLabel(String label){
		if(label == null) {
			return Optional.empty();
		}
		return Arrays.stream(values())
				.filter(m -> m.label.equalsIgnoreCase(label.trim()))
				.findFirst();
	}
	
	
	
	
}
