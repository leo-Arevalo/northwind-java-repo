
package com.la.northwind_java.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "inventory_transactions")
public class InventoryTransaction {
	/**
	 * Unique identifier for  the inventory transaction.
	 */

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true)
	private Integer id;
	
	/**
	 * Type of inventory transaction
	 */
	@NotNull(message = "Transaction type is required.")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "transaction_type", nullable = false)
	private InventoryTransactionType transactionType;
	
	/**
	 * Date when the transaction was last modified.
	 */
	@Column(name = "transaction_modified_date")
	private LocalDateTime transactionModifiedDate;
	
	/**
	 * Product involved in the transaction.
	 */
	@NotNull(message = "Product is required.")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;
	
	/**
	 * Quantity involved in the transaction.
	 */
	@NotNull(message = "Quantity is required.")
	@Column(name = "quantity", nullable = false)
	private Integer quantity;
	
	/**
	 * Purchase order associated with this transaction.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "purchase_order_id")
	private PurchaseOrder purchaseOrder;
	
	/**
	 * Customer order associated with this transaction.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_order_id")
	private Order customerOrder;
	
	/**
	 * Additional comments for the transaction.
	 */
	@Size(max = 255, message = "Comments must be at most 255 characters.")
	@Column(name = "comments", length = 255)
	private String comments;
	
	/**
	 * Purchase order details generated from this inventory transaction.
	 */
	@JsonIgnore
	@OneToMany(mappedBy = "inventoryTransaction", fetch = FetchType.LAZY)
	private List<PurchaseOrderDetail> purchaseOrderDetails = new ArrayList<>();
	
	
	
	
	
}
