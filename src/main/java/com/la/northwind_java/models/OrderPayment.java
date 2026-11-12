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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderPayment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	// Un pedido puede tener varios pagos (pagos mixtos)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "order_id", nullable = false)
	private Order order;
	
	@Column(name = "payment_method", nullable = false, length = 20)
	private String paymentMethod;
	
	@Column(name = "card_type", length = 10)
	private String cardType;
	
	@Column(name = "card_brand", length = 30)
	private String cardBrand;
	
	@Column(name = "card_last4", length = 4)
	private String cardLast4;
	
	@Column(name = "installments")
	private Integer installments;
	
	@Column(name = "pos_voucher_number", length = 30)
	private String posVoucherNumber;
	
	@Column(name = "authorization_code", length = 20)
	private String authorizationCode;
	
	@Column(name = "transfer_reference", length = 50)
	private String transferReference;
	
	@Column(name = "transfer_bank", length = 50)
	private String transferBank;
	
	@Column(name = "amount", nullable = false, precision = 19, scale = 4)
	private BigDecimal amount;
	
	
	@Column(name = "paid_at", nullable = false)
	private LocalDateTime paidAt;
	
	
	
	
	
	
	
	
	
}
