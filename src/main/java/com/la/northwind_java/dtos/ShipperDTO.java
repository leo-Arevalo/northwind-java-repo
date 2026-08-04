package com.la.northwind_java.dtos;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for transferring Shipper data.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Getter
@Setter
public class ShipperDTO {

    private Integer id;

   
    
    /*
	 * Company name of the shipper.
	 */
	@NotBlank(message = "Company name cannot be empty.")
	@Size(min = 2, max = 50, message = "Company name must be between 2 and 50 characters.")
	private String company;
	
	
	/**
	 * Last name of the shipper contact.
	 */
	@Size(max = 50)
	private String lastName;
	
	/**
	 * First name of the shipper contact.
	 */
	@Size(max = 50)
	private String firstName;
	
	/**
	 * Email address of the shipper.
	 */
	@Email(message = "invalid email format")
	@Size(max = 50, message = "Email must not exceed 50 characters.")
	private String email;
	
	/**
	 * Job title of the contact person.
	 */
	@Size(max = 50, message = "Job title must not exceed 50 characters.")
	private String jobTitle;
	
	@Size(max = 25, message = "Business phone must not exceed 25 characters.")
	@Pattern(regexp = "^\\+?[0-9\\-\\s]+$", message = "Invalid phone number format")
	private String businessPhone;

	@Size(max = 25, message = "Home phone must not exceed 25 characters.")
	@Pattern(regexp = "^\\+?[0-9\\-\\s]+$", message = "Invalid phone number format")
	private String homePhone;

	@Size(max = 25, message = " Mobile phone must not exceed 25 characters.")
	@Pattern(regexp = "^\\+?[0-9\\-\\s]+$", message = "Invalid phone number format")
	private String mobilePhone;
	
	@Size(max = 25, message = " Fax phone must not exceed 25 characters.")
	@Pattern(regexp = "^\\+?[0-9\\-\\s]+$", message = "Invalid phone number format")
	private String faxNumber;
	
	/**
	 * address details
	 */
	@Lob
	private String address;
	
	/**
	 * city of operation
	 */
	@Size(max = 50)
	private String city;

	/**
	 * State or Province
	 */
	@Size(max = 50)
	private String stateProvince;
	
	@Size(max = 15)
	private String zipPostalCode;
	
	@Size(max = 50)
	private String countryRegion;
	
	@Lob
	private String webPage;
	@Lob
	private String notes;
	@Lob
	private byte[] attachments;
	
	
	
    
}
