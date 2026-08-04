

package com.la.northwind_java.dtos.supplier;

import org.hibernate.validator.constraints.URL;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierCreateDTO {
	
	@NotBlank(message = "Company name is required")
	@Size(max = 50, message = "Company name must not exceed 50 characters.")
	private String company;
	
	@NotBlank(message = "Last Name is requiered")
	@Size(max = 50, message = "Last Name must not exceed 50 characters.")
	private String lastName;
	
	@NotBlank(message = "First Name is requiered")
	@Size(max = 50, message = "First Name must not exceed 50 characters.")
	private String firstName;
	
	@Email(message = "Invalid email format")
	@NotBlank(message = "Email is required")
	@Size(max = 50, message = "Email must not exceed 50 characters")
	private String emailAddress;
	
	@Size(max = 50, message = "Job Title must not exceed 50 characters.")	
	private String jobTitle;
	
	@Pattern(regexp = "^(|\\+?[0-9\\s\\-().]{7,20})$", message = "Invalid phone number format.")
	@Size(max = 25, message = "Busines phone must not exceed 25 characters.")
	private String businessPhone;
	
	@Pattern(regexp = "^(|\\+?[0-9\\s\\-().]{7,20})$", message = "Invalid phone number format.")
	@Size(max = 25, message = "Home phone must not exceed 25 characters.")
	private String homePhone;
	
	@Pattern(regexp = "^(|\\+?[0-9\\s\\-().]{7,20})$", message = "Invalid phone number format.")
	@Size(max = 25, message = "Mobile phone must not exceed 25 characters.")
	private String mobilePhone;
	
	@Pattern(regexp = "^(|\\+?[0-9\\s\\-().]{7,20})$", message = "Invalid fax number format.")
	@Size(max = 25, message = "fax number must not exceed 25 characters.")
	private String faxNumber;
	
	@Lob
	@Size(max = 60, message = "Address must not exceed 60 characters.")
	private String address;
	
	@Size(max = 50, message = "City must not exceed 50 characters.")
	private String city;
	
	@Size(max = 50, message = "State/Province must not exceed 50 characters.")
	private String stateProvince;
	
	@Pattern(regexp =  "^(|\\d{5}(-\\d{4})?)$", message = "Invalid zip/postal code format")
    @Size(max = 5, message = "Postal code must not exceed 5 characters")
	private String zipPostalCode;
	
	@Size(max = 50, message = "Country/Region must not exceed 50 characters.")
	private String countryRegion;
	
	@URL(message = "Invalid URL format.")
	@Lob
	private String webPage;
	
	@JsonIgnore
	@Lob
	private String notes;
	@JsonIgnore
	private byte[] attachments;
	
	
	
	
}
