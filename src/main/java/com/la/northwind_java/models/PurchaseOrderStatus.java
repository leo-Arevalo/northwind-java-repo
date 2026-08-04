package com.la.northwind_java.models;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents the status of a purchase order.
 */
@Entity
@Table(name = "purchase_order_status")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderStatus implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Unique identifier for the status.
     */
    @Id
    private Integer id;

    /**
     * Status description (e.g., Pending, Approved, Rejected).
     */
    @Size(max = 50)
    @NotBlank(message = "Status cannot be blank")
    @Column(name = "status", length = 50, nullable = false)
    private String status;
}