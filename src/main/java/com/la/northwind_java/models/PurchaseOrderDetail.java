
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
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;





@NoArgsConstructor
@AllArgsConstructor
@Data
@Setter
@Getter
@Entity
@Table(name = "purchase_order_details")
public class PurchaseOrderDetail {

	/**
	 * Unique identifier for the purchase order detail.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true)
	private Integer id;
	
	/**
	 * Purchase order associated with this detail.
	 */
	@NotNull(message = "Purchase order is required. ")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "purchase_order_id", nullable = false)
	private PurchaseOrder purchaseOrder;
	
	/**
	 * Product associated with this purchase order detail.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id")
	private Product product;
	
	/**
	 * Quantity of products ordered.
	 */
	@NotNull(message = "Quantity is required.")
	@DecimalMin(value = "0.0001", message = "Quantity must be greater than zero.")
	@Digits(integer = 14, fraction = 4)
	@Column(name = "quantity", precision = 18, scale = 4, nullable = false)
	private BigDecimal quantity;
	
	/**
	 * Cost per unit of the product.
	 */
	@NotNull(message = "Unit cost is required.")
	@DecimalMin(value = "0.0000", inclusive = true, message = "Unit cost cannot be negative. ")
	@Digits(integer = 15, fraction = 4)
	@Column(name = "unit_cost", precision = 19, scale = 4, nullable = false)
	private BigDecimal unitCost;
	
	/**
	 * Date when the products were received.
	 */
	@Column(name = "date_received")
	private LocalDateTime dateReceived;
	
	/**
	 * Indicates whether the received products have been posted to inventory.
	 */
	@NotNull(message = "Posted to inventory flag is required.")
	@Column(name = "posted_to_inventory", nullable = false)
	private Boolean postedToInventory = false;
	
	/**
	 * Inventory transaction associated with this purchase.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "inventory_id")
	private InventoryTransaction inventoryTransaction;
	
	
	
}
