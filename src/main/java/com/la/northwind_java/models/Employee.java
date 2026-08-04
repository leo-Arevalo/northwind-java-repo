package com.la.northwind_java.models;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.URL;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;




/**
 * 
 * @author LeO
 *Entity representing an Employee
 */

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "employees")
public class Employee {

	/**
	 * Unique identifier for the employee
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true)
	private Integer id;
	
	/**
	 * Company associated with the employee.
	 */
	@Size(max = 50, message = "Company name must be at most 50 characters.")
	private String company;
	
	/**
	 * Employee last name.
	 */
	@Size(max =50, message = "Last name must be at most 50 characters.")
	@Column(name = "last_name", length = 50)
	private String lastName;
	/**
	 * Employee first name
	 */
	@Size(max = 50, message = "First name must be at most 50 characters.")
	@Column(name = "first_name", length = 50)
	private String firstName;
	
	/**
	 * Employee email address.
	 */
	@Email(message = "Invalid email format.")
	@Size(max = 50, message = "Email address must be at most 50 characters.")
	@Column(name = "email_address", length = 50)
	private String email;
	
	/**
	 * Employee job title.
	 */
	@Size(max = 50, message = "Job title must be at most 50 characters.")
	@Column(name = "job_title", length = 50)
	private String jobTitle;
	
	/**
	 * Employee home phone number.
	 */
	@Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Invalid phone number format.")
	@Column(name = "home_phone", length = 25)
	private String homePhone;
	

	/**
	 * Employee mobile phone number.
	 */
	@Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Invalid phone number format.")
	@Column(name = "mobile_phone", length = 25)
	private String mobilePhone;
	

	/**
	 * Employee fax number.
	 */
	@Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Invalid fax number format.")
	@Column(name = "fax_number", length = 25)
	private String faxNumber;
	
	/**
	 * Employee address.
	 */
	@Column(name = "address", columnDefinition = "LONGTEXT")
	private String address;
	
	
	
	/**
	 * Employee city
	 */
	@Size(max = 50, message = "City must be at most 50 characters.")
	@Column(name = "city", length = 50)
	private String city;
	
	/**
     * Employee state or province.
     */
    @Size(max = 50, message = "State/Province must be at most 50 characters.")
    @Column(name = "state_province", length = 50)
    private String stateProvince;

    /**
     * Employee ZIP or postal code.
     */
    @Pattern(regexp = "^\\d{5}(-\\d{4})?$", message = "Invalid zip/postal code format.")
    @Size(max = 15, message = "Zip/Postal code must be at most 15 characters.")
    @Column(name = "zip_postal_code", length = 15)
    private String postalCode;

    /**
     * Employee country or region.
     */
    @Size(max = 50, message = "Country/Region must be at most 50 characters.")
    @Column(name = "country_region", length = 50)
    private String countryRegion;

    /**
     * Employee web page.
     */
    @URL(message = "Invalid URL format.")
    @Lob
    @Column(name = "web_page", columnDefinition = "LONGTEXT")
    private String webPage;

    /**
     * Additional employee notes.
     */
    @JsonIgnore
    @Lob
    @Column(name = "notes", columnDefinition = "LONGTEXT")
    private String notes;

    /**
     * Employee attachments.
     */
    @JsonIgnore
    @Column(name = "attachments", columnDefinition = "LONGBLOB")
    private byte[] attachments;
    
    
    /**
     * Orders assigned to this employee.
     */
    @JsonIgnore
    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();
    
    
    /**
     * Purchase orders created by this employee.
     */
    @JsonIgnore
    @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
    private List<PurchaseOrder> createdPurchaseOrders = new ArrayList<>();
    
    /**
     * Purchase orders approved by this employee.
     */
    @JsonIgnore
    @OneToMany(mappedBy = "approvedBy", fetch = FetchType.LAZY)
    private List<PurchaseOrder> approvedPurchaseOrders = new ArrayList<>();
    
    /**
     * Purchase Orders submitted by this employee.
     */
    @JsonIgnore
    @OneToMany(mappedBy = "submittedBy", fetch = FetchType.LAZY)
    private List<PurchaseOrder> submittedPurchaseOrders = new ArrayList<>();
    
    /**
     * Privileges assigned to this employee.
     */
    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
    		name = "employee_privileges",
    		joinColumns = @JoinColumn(name = "employee_id"),
    		inverseJoinColumns = @JoinColumn(name = "privilege_id")
    		)
    private List<Privileges> privileges = new ArrayList<>();
    
    /**
     * Normalize fields before persisting or updating.
     */
    @PrePersist
    @PreUpdate
    private void normalizeFields() {

        if (email != null) {
            email = email.toLowerCase().trim();
        }

        if (firstName != null) {
            firstName = firstName.trim();
        }

        if (lastName != null) {
            lastName = lastName.trim();
        }

        if (company != null) {
            company = company.trim();
        }
    }
	
	
	
}
