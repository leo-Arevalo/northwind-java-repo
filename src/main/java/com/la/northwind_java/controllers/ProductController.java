package com.la.northwind_java.controllers;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.la.northwind_java.services.ProductService;
import com.la.northwind_java.dtos.product.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/products")
@Tag(name = "Product", description = "REST API for Product management.")
@CrossOrigin("*")
public class ProductController {

	private final ProductService productService;
	
	public ProductController(ProductService productService) {
		this.productService = productService;
	}
	
	@Operation(summary ="Get a paginated list of products", description = "Requires ADMIN or EMPLOYEE role")
	@GetMapping
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
	public ResponseEntity<Page<ProductDTO>> getAllProducts(@ParameterObject Pageable pageable){
		Page<ProductDTO> products = productService.getAllProducts(pageable);
		return ResponseEntity.ok(products);
	}
	
	@Operation(summary = "Get a product by ID", description = "Requires ADMIN or EMPLOYEE role")
	@GetMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
	public ResponseEntity<ProductDTO> getProductById(@PathVariable Integer id){
		ProductDTO product = productService.getPRoductById(id);
		return ResponseEntity.ok(product);
	}
	
	@Operation(summary = "Create a new product", description = "Requires ADMIN role")
	@PostMapping
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductCreateDTO productCreateDTO){
		ProductDTO product = productService.createProduct(productCreateDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(product);
	}
	@Operation(summary = "Update an existing product", description = "Requires ADMIN role")
	@PutMapping("/{id}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<ProductDTO> updateProduct(@PathVariable Integer id, @Valid @RequestBody ProductUpdateDTO productUpdateDTO){
		ProductDTO product = productService.updateProduct(id, productUpdateDTO);
		return ResponseEntity.ok(product);
	}

	@Operation(summary = "Delete a product by ID", description = "Requires ADMIN role")
	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<Void> deleteProduct(@PathVariable Integer id){
		productService.deleteProduct(id);
		return ResponseEntity.noContent().build();
	}
	
	
	
	
	
	
	
	
	
	
}
