package com.la.northwind_java.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "invoices")
public class Invoice {

	/**
	 * Unique identifier for the invoice.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true)
	private Integer id;
	
	/**
	 * Order associated with this invoice.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	private Order order;
	
	/**
	 * Invoice issue date.
	 */
	@Column(name = "invoice_date")
	private LocalDateTime invoiceDate;
	
	/**
	 * Invoice due date.
	 */
	@Column(name = "due_date")
	private LocalDateTime dueDate;
	
	/**
	 * Tax amount.
	 */
	@DecimalMin(value = "0.0000", message = "Tax cannot be negative.")
	@Column(name = "Tax", precision = 19, scale = 4)
	private BigDecimal tax;
	
	/**
	 * Shipping amount.
	 */
	@DecimalMin(value = "0.0000", message = "Shipping cannot be negative.")
	@Column(name = "shipping", precision = 19, scale = 4)
	private BigDecimal shipping;
	
	/**
	 * Total amount due.
	 */
	@DecimalMin(value = "0.0000", message = "Amount due cannot be negative.")
	@Column(name = "amount_due", precision = 19, scale = 4)
	private BigDecimal amountDue;
	
	
	
	
}
