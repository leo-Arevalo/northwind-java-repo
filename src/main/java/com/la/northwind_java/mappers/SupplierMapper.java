
package com.la.northwind_java.mappers;

import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.la.northwind_java.dtos.supplier.SupplierCreateDTO;
import com.la.northwind_java.dtos.supplier.SupplierDTO;
import com.la.northwind_java.dtos.supplier.SupplierUpdateDTO;
import com.la.northwind_java.models.Supplier;

@Mapper(componentModel = "spring")
public interface SupplierMapper {

	/**
	 * Converts a Supplier entity to a SupplierDTO
	 */
	
	@Mapping(source = "id", target = "id")
	@Mapping(source = "company", target = "company")
	@Mapping(source = "lastName", target = "lastName")
	@Mapping(source = "firstName", target = "firstName")
	@Mapping(source = "emailAddress", target = "emailAddress")
	@Mapping(source = "jobTitle", target = "jobTitle")
	@Mapping(source = "businessPhone", target = "businessPhone")
	@Mapping(source = "homePhone", target = "homePhone")
	@Mapping(source = "mobilePhone", target = "mobilePhone")
	@Mapping(source = "faxNumber", target = "faxNumber")
	@Mapping(source = "address", target = "address")
	@Mapping(source = "city", target = "city")
	@Mapping(source = "stateProvince", target = "stateProvince")
	@Mapping(source = "zipPostalCode", target = "zipPostalCode")
	@Mapping(source = "countryRegion", target = "countryRegion")
	@Mapping(source = "webPage", target = "webPage")
	@Mapping(source = "notes", target = "notes")
	@Mapping(source = "attachments", target = "attachments")
	
	SupplierDTO toSupplierDTO(Supplier supplier);
	
	/**
	 * Convierte un SupplierCreateDTO a un nuevo Supplier entity
	 */
	@Mapping(target = "id", ignore = true)//generado por la base de datos
	@Mapping(target = "purchaseOrders", ignore = true)//evitar asignar pedidos
	
	Supplier toSupplier(SupplierCreateDTO dto);
	
	/**
	 * Actualiza un Supplier existente con valores de SupplierUpdateDTO
	 */
	void updateSupplierFromDTO(SupplierUpdateDTO dto, @MappingTarget Supplier supplier);
	
	
	List<SupplierDTO> toDtoList(List<Supplier> suppliers);
	
	
	
	
	/**
	 * Normalizo ciertos campos despues del mapeo
	 */
	
	@AfterMapping
	default void normalize(@MappingTarget Supplier supplier) {
		if(supplier.getFirstName() != null) {
			supplier.setFirstName(supplier.getFirstName().trim());
		}
		if(supplier.getLastName() != null) {
			supplier.setLastName(supplier.getLastName().trim());
		}
		if(supplier.getEmailAddress() != null) {
			supplier.setEmailAddress(supplier.getEmailAddress().trim().toLowerCase());
		}
	}
	
	
	
	
}
