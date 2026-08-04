package com.la.northwind_java.models;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.validator.constraints.URL;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "customers")

public class Customer {
	
	/*
	 * Unique identifier for the customer.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id",unique = true)
	private Integer id;
	
	/*
	 * Company name associated with the customer.
	 */
	@Size(max = 50, message = "Company name must be at most 50 characters.")
	@Column(name="company", length = 50, nullable = false)
	private String company;
	
	/*
	 * Last name of the customer.
	 */
	@NotBlank(message = "Last name cannot be blank.")
	@Size(max = 50, message = "Last name must be at most 50 characters.")
	@Column(name = "last_name", length = 50, nullable = false)
	private String lastName;
	
	/*
	 * First name of the customer.
	 */
	@NotBlank(message = "First name cannot be blank.")
	@Size(max = 50, message = "First name must be at most 50 characters.")
	@Column(name = "first_name", length = 50, nullable = false)
	private String firstName;
	
	/*
	 * Email address of the customer.
	 */
	@NotBlank(message = "Email address cannot be blank.")
	@Email(message = "Invalid email format.")
	@Size(max = 50, message = "Email address must be at most 50 characters.")
	@Column(name = "email_address", unique = true, length = 50, nullable = false)
	private String email;
	/*
	 *    Job Title of the customer.
	 */
	@Size(max = 50, message = "Job title must be at most 50 characters.")
	@Column(name = "job_title", length = 50)
	private String jobTitle;
	/*
	 *     Business phone number.
	 */
	@Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Invalid phone number format")
	@Column(name = "business_phone", length = 25)
	private String businessPhone;
	/*
	 *     Home phone number .
	 */
	@Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Invalid phone number format")
	@Column( name = "home_phone", length = 25)
	private String homePhone;
	/*
	 *     Mobile phone number.
	 */
	@Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Invalid phone number format")
	@Column( name = "mobile_phone", length = 25)
	private String mobilePhone;
	/*
	 *     Fax number.
	 */
	@Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Invalid phone number format")
	@Column(name = "fax_number", length = 25)
	private String faxNumber;
	/*
	 *     Address of the customer.
	 */
	@Lob
	@Column(name = "address")
	private String address;
	/*
	 *    City of the customer.
	 */
	@Size(max = 50, message = "City must be at most 50 characters.")
	@Column(name = "city", length = 50)
	private String city;
	/*
	 *    State or Province of the customer.
	 */
	@Size(max = 50, message = "State/Province must be at most 50 characters.")
	@Column(name = "state_province", length = 50)
	private String stateProvince;
	/*
	 *    zip or Postal Code of the customer.
	 */
	@Pattern(regexp = "^\\d{5}(-\\d{4})?$", message = "Invalid zip/postal code format")
	@Size(max = 15, message = "Zip/Postal code must be at most 15 characters.")
	@Column(name = "zip_postal_code", length = 15)
	private String postalCode;
	
	/*
	 *     Country or region of the customer.
	 */
	@Size(max = 50, message = "Country/Region must be at most 50 characters.")
	@Column(name = "country_region", length = 50)
	private String countryRegion;
	
	/*
	 *   Web Page of the customer.
	 */
	@URL(message = "Invalid URL format")
	@Lob
	@Column(name = "web_page")
	private String webPage;
	/*
	 *  Aditional notes of the customer.
	 */
	@JsonIgnore
	@Lob
	@Column(name = "notes")
	private String notes;
	/*
	 *    Attachments associated with the customer.
	 */
	@JsonIgnore
	@Lob
	@Column(name ="attachments") 
	private byte[] attachments;
	

    /**
     * Orders associated with this customer.
     */

	@JsonIgnore
	@OneToMany(mappedBy = "customer", fetch = FetchType.LAZY,
				orphanRemoval = true, cascade = CascadeType.ALL)
	private List<Order> orders = new ArrayList<>();
	
	/**
	 * Normalize fields before persisting or updating
	 */
	@PrePersist
	@PreUpdate
	private void normalizeFields() {
		if(email != null) {
			email = email.toLowerCase().trim();
		}
		if(firstName != null) {
			firstName = firstName.trim();
		}if(lastName != null) {
			lastName = lastName.trim();
		}
	}
	
	



	


}
