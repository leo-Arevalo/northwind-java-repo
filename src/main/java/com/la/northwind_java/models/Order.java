package com.la.northwind_java.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.BatchSize;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * @author LeO
 *Entity representing an order.
 */

@Entity
@Table(name = "orders", indexes ={
		@Index(name = "idx_order_date", columnList = "order_date"),
		@Index(name = "idx_status_id", columnList = "status_id"),
		@Index(name = "idx_customer_id", columnList = "customer_id"),
		@Index(name = "idx_employee_id", columnList = "employee_id"),
		@Index(name = "idx_shipped_date", columnList = "shipped_date")
		
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

	/**
	 * Unique identifier for the order.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	/**
	 * Customer who placed the order.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id")
	private Customer customer;
	
	/**
	 * Employee associated with the order.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee_id")
	private Employee employee;
	
	/**
	 * Date when the order was placed.
	 */
	@PastOrPresent(message = "Order date must be in the past or present")
	@Column(name = "order_date")
	private LocalDateTime orderDate;

	/**
	 * Date when the order was shipped
	 */
	@PastOrPresent(message = "Shipped date must be in the past or present.")
	@Column(name = "shipped_date")
	private LocalDateTime shippedDate;

	/**
	 * Shipper handling the order.
	 */
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "shipper_id")
	private Shipper shipper;

	/**
	 * Shipping Details
	 */
	@Size(max = 50)
	@Column(name = "ship_name", length = 50)
	private String shipName;

	/**
	 * Shipping address.
	 */
	@Lob
	@Column(name = "ship_address")
	private String shipAddress;

	/**
	 * SHipping city.
	 */
	@Size(max = 50)
	@Column(name = "ship_city", length = 50)
	private String shipCity;

	/**
	 * Shipping state or province.
	 */
	@Size(max = 50)
	@Column(name = "ship_state_province", length = 50)
	private String shipStateProvince;

	/**
	 * Shipping postal code.
	 */
	@Size(max = 50)
	@Column(name = "ship_zip_postal_code", length = 50)
	private String shipZipPostalCode;

	/**
	 * Shipping country or region.
	 */
	@Size(max = 50)
	@Column(name = "ship_country_region", length = 50)
	private String shipCountryRegion;

	
	/**
	 * Shipping fee
	 */
	@DecimalMin(value = "0.00", message = "Shipping fee must be positive")
	@Digits(integer = 36, fraction = 2)
	@Column(name = "shipping_fee", precision = 38, scale = 2, nullable = false)
	private BigDecimal shippingFee;
	
	/**
	 * Taxes applied to the order
	 */
	
	@DecimalMin(value = "0.00", message = "taxes must be positive")
	@Digits(integer = 36, fraction = 2)
	@Column(name = "taxes", precision = 38, scale = 2, nullable = false)
	private BigDecimal taxes;
	
	
	/**
	 * Payment type
	 */
	@Size(max = 50)
	@Column(name = "payment_type", length = 50)
	private String paymentType;
	

	/**
	 * Date when the order was paid.
	 */
	@Column(name = "paid_date")
	private LocalDateTime paidDate;
	
	/**
	 * Additional notes.
	 */
	@Lob
	@Column(name = "notes")
	private String notes;
	
	/**
	 * tax rate.
	 */
	@DecimalMin(value = "0.00", message = "Tax rate must be positive")
	@Column(name = "tax_rate")
	private Double taxRate;
	
	
	@JsonIgnore
	@OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
	private List<Invoice> invoices = new ArrayList<>();
	
	/**
	 * Tax status.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tax_status_id")
	private OrderTaxStatus taxStatus;
	
	/**
	 * Order status.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "status_id")
	private OrderStatus status;
	
	/**
	 * Order details associated with this order.
	 */
	@JsonIgnore
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@BatchSize(size = 10)
	@OrderBy("id ASC")
	private List<OrderDetails> orderDetails = new ArrayList<>();
	
	   /**
     * Normalize fields before persisting or updating.
     */
    @PrePersist
    @PreUpdate
    private void normalizeData() {
        if (shipName != null) {
            shipName = shipName.trim();
        }
        if (shipCity != null) {
            shipCity = shipCity.trim();
        }
        if (shippedDate != null && orderDate != null && shippedDate.isBefore(orderDate)) {
            throw new IllegalStateException("Shipped date cannot be before order date.");
        }
    }

	

	
	

	
	
}
