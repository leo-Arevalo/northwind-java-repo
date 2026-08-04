package com.la.northwind_java.dtos;

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
 * Entity representing order tax status.
 * 
 * @author LeO
 *
 */
@Entity
@Table(name = "orders_tax_status")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class OrderTaxStatusDTO {

	/**
	 * Unique identifier for the tax status.
	 */
	@Id
	@Column(name = "id")
	private Byte id;
	
	@NotBlank(message = "Tax status name is required")
	@Size(max = 50, message = "Tax status name must not exceed 50 characters")
	@Column(name = "tax_status_name", nullable = false, length = 50)
	private String taxStatusName;
	
}



