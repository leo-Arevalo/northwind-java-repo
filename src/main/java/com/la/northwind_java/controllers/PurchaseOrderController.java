
package com.la.northwind_java.controllers;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.la.northwind_java.dtos.purchaseOrder.*;
import com.la.northwind_java.services.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/purchase-orders")
@RequiredArgsConstructor
@Tag(name= "PurchaseOrder", description = "Operation related to purchase orders.")
public class PurchaseOrderController {

	private final PurchaseOrderService purchaseOrderService;
	
	@Operation(summary = "Get all purchase orders", description = "Get paginated list of order with filters.")
	@GetMapping
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_EMPLOYEE')")
	public ResponseEntity<Page<PurchaseOrderDTO>> getAll(
			@ModelAttribute PurchaseOrderSearchDTO searchDto,
			@ParameterObject Pageable pageable) {
		return ResponseEntity.ok(purchaseOrderService.getOrders(searchDto, pageable));
	}
	
	@Operation(summary = "Get purchase order by ID")
	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_EMPLOYEE')")
	public ResponseEntity<PurchaseOrderDTO> getById(@PathVariable Integer purchaseOrderId){
		return ResponseEntity.ok(purchaseOrderService.getById(purchaseOrderId));
	}
	
	
	@GetMapping("/by-supplier/{supplierId}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_EMPLOYEE')")
	public ResponseEntity<List<PurchaseOrderDTO>> getBySupplier(@PathVariable Integer supplierId){
		List<PurchaseOrderDTO> orders = purchaseOrderService.getBySupplierId(supplierId);
		return ResponseEntity.ok(orders);
	}
	
	@Operation(summary = "Create purchase order")
	@PostMapping
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<PurchaseOrderDTO> create(@Valid @RequestBody PurchaseOrderCreateDTO dto){
		return ResponseEntity.status(HttpStatus.CREATED).body(purchaseOrderService.create(dto));
	}
	@Operation(summary = "Update purchase order")
	@PutMapping("/{id}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<PurchaseOrderDTO>update(@PathVariable Integer id, @Valid @RequestBody PurchaseOrderUpdateDTO dto){
		return ResponseEntity.ok(purchaseOrderService.update(id,dto));
	}
	@Operation(summary = "Delete purchase Order")
	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<Void> delete(@PathVariable Integer id){
		purchaseOrderService.delete(id);
		return ResponseEntity.noContent().build();
	}
	
	@Operation(summary = "Get orders by status", description = "Retrieve purchase order by a status string like 'PENDING', 'APPROVED', etc.")
	@GetMapping("/status/{status}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') OR hasAuthority('ROLE_EMPLOYEE')")
	public ResponseEntity<List<PurchaseOrderDTO>> getByStatus(@PathVariable String status){
		List<PurchaseOrderDTO> orders = purchaseOrderService.getByStatus(status);
		return ResponseEntity.ok(orders);
	}
	
	
}
