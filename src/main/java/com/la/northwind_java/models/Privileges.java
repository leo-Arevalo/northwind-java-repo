package com.la.northwind_java.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "privileges")
public class Privileges {

	/**
	 * Unique identifier for the privilege.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true)
	private Integer id;
	
	/**
	 * Name of the privilege.
	 */
	@NotBlank(message = "Privilege name cannot be blank.")
	@Size(max = 50, message = "Privilege name must be at most 50 characters.")
	@Column(name = "privilege_name", nullable = false, length = 50)
	private String privilegeName;
	
	/**
	 * Employees assigned to this privilege.
	 */
	@JsonIgnore
	@ManyToMany(mappedBy = "privileges")
	private List<Employee> employees = new ArrayList<>();
	
	
	
	
}
