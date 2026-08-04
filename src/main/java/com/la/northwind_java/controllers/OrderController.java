

package com.la.northwind_java.controllers;



import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation; //documentacion openAPI
import io.swagger.v3.oas.annotations.responses.ApiResponse; //documentacion openAPI
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import com.la.northwind_java.dtos.OrderDTO;
import com.la.northwind_java.dtos.OrderUpdateDTO;
import com.la.northwind_java.dtos.customer.CustomerDTO;
import com.la.northwind_java.services.OrderService;



import lombok.RequiredArgsConstructor;



@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor //al usar el constructor no necesitamos autowired
@Tag(name = "Orders", description = "Operations related to orders")
public class OrderController {
	
	private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
	private final OrderService orderService;
	
	
	/**
     * Retrieves a paginated list of orders with optional filtering by customerId, employeeId, and status.
 
     * 
     */
	
	@GetMapping
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_USER')")
	@Operation(summary = "Get paginated list of orders",
				description = "Retrieves a paginated list of orders with optional filtering. require ADMIN, EMPLOYEE,"+
							" or CUSTOMER (Only own orders)")
	@ApiResponse(responseCode = "200", description = "Successful retrieval of orders.")
	@Cacheable(value = "orders")
	
	public ResponseEntity<Page<OrderDTO>> getAllOrders(
			@RequestParam(defaultValue = "0") @Min(0) int page,
			@RequestParam(defaultValue = "10") @Min(1) int size,
			@RequestParam(required = false) Integer customerId,
			@RequestParam(required = false) Integer employeeId,
			@RequestParam(required = false) String status,
			@RequestParam(defaultValue = "orderDate") String sortBy,
			@RequestParam(defaultValue = "asc") String sortDirection 
			){
	/*	
		logger.info("Fetching orders with filters - page: {}, size: {}, customerId: {}, employeeId: {}, status: {}",
				page, size, customerId, employeeId, status);
	*/	
		Page<OrderDTO> orders = orderService.getAllOrders(page, size, customerId, employeeId, status, sortBy, sortDirection);				
		return ResponseEntity.ok(orders);
	}
	

	/*
	 *     * * ROLE_ADMIN Y ROLE_EMPLOYEE permite consultar a los admin y empleados
	 * #id == authentication.principal.id Permite a un cliente consultar sus propios pedido y no los de otros clientes.
	 */

//	@GetMapping("/customer/{id}/orders")
//	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_EMPLOYEE') OR #id = authentication.principal.id")
//	public ResponseEntity<List<CustomerDTO>> getCustomerOrders(@PathVariable Long id){
//		return ResponseEntity.ok(orderService.getOrdersByCustomer(id));	
//	}
	
	
	
	
	
	
	/**
	 * Retrieves details of a specific order by ID.
	 * @param id
	 * @return
	 */
	
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_CUSTOMER')")
	@GetMapping("/{id}")
	@Operation(summary = "Get order details", description = "Retrieves details of a specific order by ID.")
	@ApiResponse(responseCode = "200", description = "Order details retrieved successfully")
	@ApiResponse(responseCode = "404", description = "Order not found")
	public ResponseEntity<OrderDTO> getOrderById(@PathVariable Integer id){
		//logger.info("Fetching order details for ID: {}",id);
		return ResponseEntity.ok(orderService.getOrderById(id));
	}
	
	
	
	
	
	
	/**
	 * Create a new Order
	 * @param orderDTO
	 * @return
	 */
	
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@PostMapping
	@Operation(summary = "Create a new order", description = "Creates a new order with the provided details.")
	@ApiResponse(responseCode = "201", description = "Order created successfully")
	@Transactional
	
	public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody OrderDTO orderDTO){
		//logger.info("Creating new Order");
		OrderDTO createdOrder = orderService.createOrder(orderDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
	}
	
	/**
	 * Update an existing order
	 * @param id
	 * @param orderDTO
	 * @return
	 */
	
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@PutMapping("/{id}")
	@Operation(summary = "Update an existing order",
				description = "Update an existing order based on the provided ID and details.")
	@ApiResponse(responseCode = "200", description = "Order updated successfully")
	@ApiResponse(responseCode = "404", description = "Order not found")
	public ResponseEntity<OrderDTO> updateOrder(@PathVariable Integer id,
												@Valid @RequestBody OrderUpdateDTO orderUpdateDTO){
		//logger.info("Updating order with ID: {}", id);
		return ResponseEntity.ok(orderService.updateOrder(id, orderUpdateDTO));
	}
	
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@DeleteMapping("/{id}")
	@Operation(summary = "Delete an order",
				description = "Deletes an order based on the provided ID.")
	@ApiResponse(responseCode = "204", description = "Order delete successfully")
	@ApiResponse(responseCode = "404", description = "Order not found")
	public ResponseEntity<Void> deleteOrder(@PathVariable Integer id){
		//logger.info("Deleting order with ID: {}", id);
		orderService.deleteOrder(id);
		return ResponseEntity.ok().build();
	}
	
	
}







