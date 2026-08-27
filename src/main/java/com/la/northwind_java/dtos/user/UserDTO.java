package com.la.northwind_java.dtos.user;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

	private Long id;
	private String username;
	private String email;
	private boolean enabled;
	private Set<String> roles;
	//Datos del Employee vinculado (si tiene).
	//Null si la cuenta no tiene un empleado de negocio asociado.
	private Integer employeeId;
	private String employeeFullName;
	private Set<String> privileges;
}
