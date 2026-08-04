
package com.la.northwind_java.dtos.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@Builder
@Getter
@Setter
public class CustomerCreateDTO {

	@NotBlank(message = "Customer name is required")
	@Size(max = 100, message = "Customer name must not exceed 100 characters")
	private String name;
	
	@Email(message = "Invalid email format")
	@NotBlank(message = "Email is required")
	@Size(max = 150, message = "Email must not exceed 150 characters.")
	private String email;
	
	@Size(max = 20, message = "Phone number must not exceed 20 characters.")
	private String phone;
	
	@Size(max = 250, message = "Address must not exceed 250 characters.")
	private String address;
	
	@NotNull(message = "Active status is required")
	private Boolean isActive;
	
	
}
