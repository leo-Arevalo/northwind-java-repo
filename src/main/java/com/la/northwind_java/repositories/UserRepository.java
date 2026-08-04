package com.la.northwind_java.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.la.northwind_java.security.models.User;

public interface UserRepository extends JpaRepository<User, Long>{
	Optional<User> findByUsername(String username);
	Optional<User> findByEmail(String email);
}
