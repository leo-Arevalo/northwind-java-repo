package com.la.northwind_java.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "products")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int productID;
	
	@Column(name = "product_code", length=25)
	private String productCode;
	
	@NotBlank(message = "Product name cannot be blank")
	@Size(max = 100, message = "Product name cannot exceed 100 characters")
	@Column(name = "product_name")
	private String productName;
	
	@Column(name = "description", columnDefinition = "TEXT")
	private String description;
	
	@Column(name = "standard_cost", precision = 19, scale = 4, nullable = false)
	private BigDecimal standardCost;
	
	@Column(name = "list_price", precision = 19, scale = 4, nullable = false)
	private BigDecimal listPrice;
	
	@Column(name = "reorder_level")
	private Integer reorderLevel;
	
	@Column(name = "target_level")
	private Integer targetLevel;
	
	@Column(name = "quantity_per_unit", length = 50)
	private String quantityPerUnit;
	
	@Column(name = "discontinued", nullable = false)
	private Boolean discontinued;
	
	@Column(name = "minimum_reorder_quantity")
	private Integer minimumReorderQuantity;

	@Column(name = "category", length = 50)
	private String category;
	
	
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderDetails> orderDetails = new ArrayList<>();


	

}
