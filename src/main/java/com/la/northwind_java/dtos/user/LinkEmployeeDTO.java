package com.la.northwind_java.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LinkEmployeeDTO {
	// Null = desvincular al empleado que tuviera asignado
	private Integer employeeId;

}
