
package com.la.northwind_java.models;

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

@Entity
@Table(name = "order_tax_status")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class OrderTaxStatus {
	@Id
	@Column(name = "id")
	private byte id;
	
	@NotBlank
	@Size(max = 50)
	@Column(name = "tax_status_name", nullable = false, length = 50)
	private String taxStatusName;
	
}
