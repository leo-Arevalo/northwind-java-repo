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
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "order_details")
public class OrderDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	private Order order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="product_id")
	private Product product;
	
	@Column(name = "unit_price", precision = 19, scale = 4, nullable = false)
	private BigDecimal unitPrice;
	
	@Column(name = "quantity", precision = 18, scale = 4, nullable = false)
	private BigDecimal quantity;
	
	@Column(name = "discount", nullable = false)
	private Double discount;
	
	@Column(name = "status_id")
	private Integer statusId;
	
	@Column(name = "date_allocated")
	private LocalDateTime dateAllocated;
	
	@Column(name = "purchase_order_id")
	private Integer purchaseOrderId;
	
	@Column(name = "inventory_id")
	private Integer inventoryId;


}
