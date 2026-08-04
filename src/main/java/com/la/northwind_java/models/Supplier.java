
package com.la.northwind_java.models;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.BatchSize;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represent a supplier in the system
 * 
 * @author LeO
 */

@Entity
@Table(name = "suppliers", indexes = {
		@Index(name = "idx_city", columnList = "city"),
		@Index(name = "idx_company", columnList = "company"),
		@Index(name = "idx_last_name", columnList = "last_name"),
		@Index(name = "idx_first_name", columnList = "first_name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Supplier {
	
	/**
	 * Unique identifier for the supplier.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Size(max = 50)
	@NotBlank(message = "Company name cannot be blank")
	@Column(name = "company", length = 50)
	private String company;
	
	@Size(max = 50)
	@NotBlank(message = "Last name cannot be blank")
	@Column(name = "last_name", length = 50)
	private String lastName;
	
	@Size(max = 50)
	@NotBlank(message = "First name cannot be blank")
	@Column(name = "first_name",nullable = false, length = 50)
	private String firstName;
	
	@Email
	@Size(max = 50)
	@NotBlank(message = "Email cannot be blank")
	@Column(name = "email_address", length = 50, unique = true, nullable = false)
	private String emailAddress;
	
	@Size(max = 50)
	@Column(name = "job_title", length = 50)
	private String jobTitle;
	
	@Size(max = 25)
	@Column(name = "business_phone", length = 25)
	private String businessPhone;
	
	@Size(max = 25)
	@Column(name = "home_phone", length = 25)
	private String homePhone;
	
	@Size(max = 25)
	@Column(name = "mobile_phone", length = 25)
	private String mobilePhone;
	
	@Size(max = 25)
	@Column(name = "fax_number", length = 25)
	private String faxNumber;
	
	@Lob
	@Column(name = "address", columnDefinition = "TEXT")
	private String address;
	
	@Size(max = 50)
	@Column(name = "city", length = 50)
	private String city;
	
	@Size(max = 50)
	@Column(name = "state_province", length = 50)
	private String stateProvince;
	
	@Size(max = 15)
	@Column(name = "zip_postal_code", length = 15)
	private String zipPostalCode;
	
	@Size(max = 50)
    @Column(name = "country_region", length = 50)
    private String countryRegion;

    @Lob
    @Column(name = "web_page", columnDefinition = "TEXT")
    private String webPage;

    @Lob
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Lob
    @Column(name = "attachments")
    private byte[] attachments;
	
	/**
	 * List of purchase orders associated with this supplier.
	 */
	
	@OneToMany(mappedBy = "supplier", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.LAZY)
	@BatchSize(size = 10)
	List<PurchaseOrder> purchaseOrders = new ArrayList<>();
	
	// @CreationTimestamp
	// private LocalDateTime createdAt;
	
	// @UpdateTimestamp
	// private LocalDateTime updatedAt;
	
	
	
	/**
	 * Normalize string data before persisting or updating.
	 */
	@PrePersist
	@PreUpdate
	private void normalizeData() {
		if (company != null) {
			company = company.trim().toUpperCase();
		}
		if(city != null) {
			city = city.trim();
		}
	}
	
	
	
	
	
	
	
}
