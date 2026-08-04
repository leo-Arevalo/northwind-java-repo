package com.la.northwind_java.models;


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
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * Entity representing a shipper.
 */


@Entity
@Table(name = "shippers", indexes = {
		@Index(name = "idx_city", columnList = "city"),
		@Index(name = "idx_company", columnList = "company"),
		@Index(name = "idx_first_name", columnList = "first_name"),
		@Index(name = "idx_last_name", columnList = "last_name"),
		@Index(name = "idx_zip_postal_code", columnList = "zip_postal_code"),
		@Index(name = "idx_state_province", columnList = "state_province"),
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Shipper {

	/**
	 * Unique identifier for the shipper.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	/*
	 * Company name of the shipper.
	 */
	@NotBlank(message = "Company name cannot be empty.")
	@Size(min = 2, max = 50, message = "Company name must be between 2 and 50 characters.")
	@Column(nullable = false)
	private String company;
	
	
	/**
	 * Last name of the shipper contact.
	 */
	@Size(max = 50)
	@Column(name = "last_name")
	private String lastName;
	
	/**
	 * First name of the shipper contact.
	 */
	@Size(max = 50)
	@Column(name = "first_name")
	private String firstName;
	
	/**
	 * Email address of the shipper.
	 */
	@Email(message = "invalid email format")
	@Size(max = 50, message = "Email must not exceed 50 characters.")
	@Column(name = "email_address")
	private String email;
	
	/**
	 * Job title of the contact person.
	 */
	@Size(max = 50, message = "Job title must not exceed 50 characters.")
	@Column(name = "job_title")
	private String jobTitle;
	
	@Size(max = 25, message = "Business phone must not exceed 25 characters.")
	@Pattern(regexp = "^\\+?[0-9\\-\\s]+$", message = "Invalid phone number format")
	@Column(name = "business_phone")
	private String businessPhone;

	@Size(max = 25, message = "Home phone must not exceed 25 characters.")
	@Pattern(regexp = "^\\+?[0-9\\-\\s]+$", message = "Invalid phone number format")
	@Column(name = "home_phone")
	private String homePhone;

	@Size(max = 25, message = " Mobile phone must not exceed 25 characters.")
	@Pattern(regexp = "^\\+?[0-9\\-\\s]+$", message = "Invalid phone number format")
	@Column(name = "mobile_phone")
	private String mobilePhone;
	
	@Size(max = 25, message = " Fax phone must not exceed 25 characters.")
	@Pattern(regexp = "^\\+?[0-9\\-\\s]+$", message = "Invalid phone number format")
	@Column(name = "fax_number")
	private String faxNumber;
	
	/**
	 * address details
	 */
	@Lob
	@Column(name = "address", columnDefinition = "LONGTEXT")
	private String address;
	
	/**
	 * city of operation
	 */
	@Size(max = 50)
	@Column(name = "city")
	private String city;

	/**
	 * State or Province
	 */
	@Size(max = 50)
	@Column(name = "state_province")
	private String stateProvince;
	
	@Size(max = 15)
	@Column(name = "zip_postal_code")
	private String zipPostalCode;
	
	@Size(max = 50)
	@Column(name = "country_region")
	private String countryRegion;
	
	@Lob
	@Column(name = "web_page", columnDefinition = "LONGTEXT")
	private String webPage;
	@Lob
	@Column(name = "notes", columnDefinition = "LONGTEXT")
	private String notes;
	@Lob
	@Column(name = "attachments", columnDefinition = "LONGBLOB")
	private byte[] attachments;
	
	/**
	 * Phone number of the shipping company.
	 */
	
	
	/**
	 * List of orders handled by this shipper.
	 */
	@JsonIgnore
	@OneToMany(mappedBy = "shipper", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@BatchSize(size = 10)
	private List<Order> orders = new ArrayList<>();
	
	

	/**
	 * Normalize data before persisting or updating
	 */
	@PrePersist
	@PreUpdate
	private void normalizeData() {
		if(company != null) {
			company = company.trim().toUpperCase();
		}
		if(city != null) {
			city = city.trim();
		}
	}
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if ( o == null || getClass() != o.getClass()) return false;
		Shipper shipper = (Shipper) o;
		return id != null && shipper.id != null && id.equals(shipper.id);
	}
	
	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
	
	
	

	
}
