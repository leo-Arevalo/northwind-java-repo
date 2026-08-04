

package com.la.northwind_java.mappers;


import org.mapstruct.AfterMapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.la.northwind_java.dtos.customer.CustomerCreateDTO;
import com.la.northwind_java.dtos.customer.CustomerDTO;
import com.la.northwind_java.dtos.customer.CustomerUpdateDTO;
import com.la.northwind_java.models.Customer;


/*
 * Mapper interface for converting between Customer entites and DTOs.
 * Uses MapStruct for automatic mapping.
 */

@Mapper(componentModel = "spring")
public interface CustomerMapper {

	/**
	 * Converts a Customer entity to a CustomerDTO.
	 * Maps nested objects to their corresponding IDs.
	 */
	
	@Mapping(source = "id", target = "id")
	@Mapping(source = "company", target = "company")
	@Mapping(source = "lastName", target = "lastName")
	@Mapping(source = "firstName", target = "firstName")
	@Mapping(source = "jobTitle", target = "jobTitle")
	@Mapping(source = "email", target = "email")
	@Mapping(source = "businessPhone", target = "businessPhone")
	@Mapping(source = "homePhone", target = "homePhone")
	@Mapping(source = "mobilePhone", target = "mobilePhone")
	@Mapping(source = "faxNumber", target = "faxNumber")
	@Mapping(source = "address", target = "address")
	@Mapping(source = "city", target = "city")
	@Mapping(source = "stateProvince", target = "stateProvince")
	@Mapping(source = "postalCode", target = "postalCode")
	@Mapping(source = "countryRegion", target = "countryRegion")
	@Mapping(source = "webPage", target = "webPage")
	@Mapping(source = "notes", target = "notes")
	@Mapping(source = "attachments", target = "attachments")

	//@Mapping(target = "orders", ignore = true)

	CustomerDTO toCustomerDTO(Customer customer);
	
	/**
	 * Converts a CustomerDTO back to a Customer entity.
	 * Uses the inverse of the mappings defined in to CustomerDTO().
	 */
	//@InheritInverseConfiguration
	//@Mapping(target = "orders", ignore = true)//ignoramos la relacion con las ordenes
	//Customer toCustomer(CustomerDTO customerDTO);
	
	
	/**
	 * Creates a new Customer entity with values from a CustomerCreateDTO
	 */
	@Mapping(target = "id", ignore = true) //Id will generated automatically.
	@Mapping(target = "orders", ignore = true) //there will not asigned orders at create.
	Customer toCustomer(CustomerCreateDTO customerCreateDTO);
	
	/**
	 * Updates an existing Customer entity with values from a CustomerUpdateDTO.
	 * This does not replace associations unless explicitly provided.
	 */
	@Mapping( target = "orders", ignore = true) //No sobrescribir pedidos
	void updateEntity(CustomerUpdateDTO customerUpdateDTO, @MappingTarget Customer customer);
	
	
	
	/**
	 * Normalizes data after mapping a CustomerDTO to a Customer entity.
	 * Trims and formats certain string fields.
	 */
	
	@AfterMapping
	default void normalizeData(@MappingTarget Customer customer) {
		if(customer.getFirstName() != null) {
			customer.setFirstName(customer.getFirstName().trim());
		}
		if(customer.getLastName() != null) {
			customer.setLastName(customer.getLastName().trim());
		}
		if(customer.getEmail() != null) {
			customer.setEmail(customer.getEmail().toLowerCase().trim());
		}
	}
	
	
	
	
	
	
	
}
