package com.la.northwind_java.models;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "inventory_transaction_types")
public class InventoryTransactionType {

	/**
	 * Unique identifier for the inventory transaction type.
	 */
	@Id
	@Column(name = "id", nullable = false)
	private Byte id;
	
	/**
	 * Name of the inventory transaction type
	 */
	@NotBlank(message = "Transaction type name cannot be blank")
	@Size(max = 50, message = "Transaction type name must be at most 50 characters.")
	@Column(name = "type_name", nullable = false, length = 50)
	private String typeName;
	
	/**
	 * Inventory transactions associated with this transaction type.
	 */
	
	@JsonIgnore
	@OneToMany(mappedBy = "transactionType")
	private java.util.List<InventoryTransaction> inventoryTransactions = new ArrayList<>();
	
	
}
