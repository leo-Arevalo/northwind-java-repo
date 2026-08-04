package com.la.northwind_java.dtos;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {
	private int employeeID;
	private String lastName;
	private String firstName;
	private String title;
	private String titleOfCourtesy;
	private Timestamp birthDate;
	private Timestamp hireDate;
	private String address;
	private String city;
	private String region;
	private String postalCode;
	private String country;
	private String homePhone;
	private String extension;
	private byte photo;
	private String notes;
}




