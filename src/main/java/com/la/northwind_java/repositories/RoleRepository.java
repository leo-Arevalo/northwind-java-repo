package com.la.northwind_java.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.la.northwind_java.security.models.Role;

public interface RoleRepository extends JpaRepository<Role, Long>{
	Role findByName(String name);
}
