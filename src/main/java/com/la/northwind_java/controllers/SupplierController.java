
package com.la.northwind_java.controllers;

import com.la.northwind_java.dtos.supplier.SupplierCreateDTO;
import com.la.northwind_java.dtos.supplier.SupplierDTO;
import com.la.northwind_java.dtos.supplier.SupplierSearchDTO;
import com.la.northwind_java.dtos.supplier.SupplierUpdateDTO;
import com.la.northwind_java.services.SupplierService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
@Tag(name = "Supplier", description = "Operations related to suppliers.")
public class SupplierController {
	
	public final SupplierService supplierService;
	
	
	@Operation(summary = "Search suppliers with optional filters",
				description = "Retrieve a paginated list of suppliers with filters like company, name, location, order status, and more.")
	@GetMapping
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_EMPLOYEE')")
	public ResponseEntity<Page<SupplierDTO>> getSuppliers(
			@ModelAttribute SupplierSearchDTO searchDTO,
			@ParameterObject Pageable pageable){

		Page<SupplierDTO> suppliers = supplierService.getSuppliers(searchDTO, pageable);
		return ResponseEntity.ok(suppliers);		
		//EN POSTMAN 
		//GET /api/suppliers?company=tech&country=usa&active=true&page=0&size=5&sortBy=company&direction=asc
	}
	
	
	@GetMapping("/recent")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')  or hasAuthority('ROLE_CUSTOMER') or hasAuthority ('ROLE_USER')")
	@Operation(summary = "Get recent Supplier used.", description = "Retrieve the most recently accessed o created suppliers.")
	@ApiResponse(responseCode = "200", description = "Recent suppliers retrieved successfully.")
	
	public ResponseEntity<List<SupplierDTO>> getRecentSuppliers() {
		List<SupplierDTO> recentSuppliers = supplierService.findRecentSuppliers();
		return ResponseEntity.ok(recentSuppliers);
	}
	
	
	@Operation(summary = "Get supplier by ID", description = "Fetch a supplier by its ID")
	@ApiResponse(responseCode = "200", description = "Supplier found")
	@ApiResponse(responseCode = "404", description = "Supplier not found")
	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_EMPLOYEE')")
	public ResponseEntity<SupplierDTO> getSupplierById(@PathVariable Integer id) {
		SupplierDTO supplier = supplierService.getSupplierById(id);
		return ResponseEntity.ok(supplier);
	}
	@Operation(summary ="Create new supplier", description = "Create a new supplier with the given details")
	@ApiResponse(responseCode = "201", description = "Supplier created successfully")
	@ApiResponse(responseCode = "400", description = "Vlidation error")
	@PostMapping
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<SupplierDTO> createSupplier(@Valid @RequestBody SupplierCreateDTO createDTO){
		SupplierDTO createdSupplier = supplierService.createSupplier(createDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdSupplier);
	}
	
	
	@Operation(summary ="update supplier", description = "Update an existing supplier usin its ID and new data") 
	@ApiResponse(responseCode = "200", description = "Supplier updated successfully")
	@ApiResponse(responseCode = "404", description = "Supplier not found.")
	@PutMapping("/{id}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<SupplierDTO> updateSupplier(@PathVariable Integer id, 
													@Valid @RequestBody SupplierUpdateDTO updateDTO){
		SupplierDTO updateSupplier = supplierService.updateSupplier(id, updateDTO);
		return ResponseEntity.ok(updateSupplier);
	}
	
	@Operation(summary = "Delete supplier", description = "Deletes a supplier by ID")
	@ApiResponse(responseCode = "204", description = "Supplier deleted successfully")
	@ApiResponse(responseCode = "404", description = "Supplier not found")
	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<Void> deleteSupplier(@PathVariable Integer id){
		supplierService.deleteSupplier(id);
		return ResponseEntity.noContent().build();
	}
	
}










