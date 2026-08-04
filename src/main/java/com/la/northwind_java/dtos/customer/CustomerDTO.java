
package com.la.northwind_java.dtos.customer;

import org.hibernate.validator.constraints.URL;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter

public class CustomerDTO {

	@Id
	@NotNull(message = "Customer ID is required")
	private Long id;
	
	@NotBlank(message = "Company name is required")
	@Size(max = 50, message = "Company name must not exceed 40 characters")
	private String company;
	
	@NotBlank(message = "Last Name cannot be blank.")
	@Size(max = 50, message = "Last Name must not exceed 50 characters")
	private String lastName;
	
	@NotBlank(message = "First Name cannot be blank.")
	@Size(max = 50, message = "First Name must not exceed 50 characters.")
	private String firstName;
	
	@Size(max = 50, message = "Job Title must not exceed 50 characters")
	private String jobTitle;

	@Email(message = "Invalid email format")
	@NotBlank(message = "Email is required")
	@Size(max = 50, message = "Email must not exceed 50 characters")
	private String email;
	
	@Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Invalid phone number format")
	@Size(max = 25, message = "Phone number must not exceed 25 characters")
	private String businessPhone;
	
	@Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Invalid phone number format")
	@Size(max = 25, message = "Phone number must not exceed 25 characters")
	private String homePhone;
	
	@Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Invalid phone number format")
	@Size(max = 25, message = "Phone number must not exceed 25 characters")
	private String mobilePhone;
	
	@Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Invalid phone number format")
	@Size(max = 25, message = "Fax must not exceed 25 characters")
	private String faxNumber;
	
	@Lob
	@Size(max = 60, message = "Address must not exceed 60 characters")
	private String address;
	
	@Size(max = 50, message = "City must not exceed 50 characters")
	private String city;
	
	@Size(max = 50, message = "State Province must not exceed 50 characters")
	private String stateProvince;
	
	@Pattern(regexp = "^\\d{5}(-\\d{4})?$", message = "Invalid zip/postal code format")
	@Size(max = 15, message = "Postal Code must not exceed 10 characters")
	private String postalCode;
	
	@Size(max = 50, message = "Country must not exceed 50 characters")
	private String countryRegion;
	
	@URL(message = "Invalid URL format.")
	@Lob
	private String webPage;
	
	@JsonIgnore
	@Lob
	private String notes;

	@JsonIgnore
	@Lob
	private byte[] attachments;
	
	
	
}







