package com.la.northwind_java.controllers;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.la.northwind_java.dtos.user.LinkEmployeeDTO;
import com.la.northwind_java.dtos.user.UserDTO;
import com.la.northwind_java.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "Administracion de cuentas de usuario. Requiere ROLE_ADMIN.")
@CrossOrigin("*")
public class UserController {

	private final UserService userService;
	
	public UserController(UserService userService) {
		this.userService = userService;
	}
	@Operation(summary = "Listar usuarios (paginado)")
	@GetMapping
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<Page<UserDTO>> getAllUsers (@ParameterObject Pageable pageable){
		return ResponseEntity.ok(userService.getAllUsers(pageable));
	}
	
	@Operation(summary = "Ver un usuario por id")
	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<UserDTO> getUserById(@PathVariable Long id){
		return ResponseEntity.ok(userService.getUserById(id));
	}
	
	@Operation(summary = "Vincular (o desvincular con employeeId null) un Employee a un User")
	@PatchMapping("/{id}/employee")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<UserDTO> linkEmployee(@PathVariable Long id, @Valid @RequestBody LinkEmployeeDTO dto){
		return ResponseEntity.ok(userService.linkEmployee(id, dto.getEmployeeId()));
	}
	
	
}

