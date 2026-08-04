package com.la.northwind_java.security.jwt;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public class JWTUtilsTest {

	
	private JWTUtils jwtUtils;
	
	private UserDetails adminUser;
	private UserDetails normalUser;
	private UserDetails multiRoleUser;
	
	@BeforeEach
	void setUp() {
		
		//Clase que vamos a probar
		jwtUtils = new JWTUtils();
		
		//Usuario con un rol
		adminUser = new User(
				"leo",
				"1234",
				List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
				);
		//Otro usuario
		normalUser = new User(
				"juan",
				"1234",
				List.of(new SimpleGrantedAuthority("ROLE_USER"))
				);
		//Usuario con varios roles
		multiRoleUser = new User(
				"carlos",
				"1234",
				List.of(new SimpleGrantedAuthority("ROLE_ADMIN"),
						new SimpleGrantedAuthority("ROLE_USER"),
						new SimpleGrantedAuthority("ROLE_MANAGER")
						)
				);
	}
	
	@Test
	void generateToken_ShouldReturnToken() {
		//generamos un JWT para el usuario administrador
		String token = jwtUtils.generateToken(adminUser);
		
		//verificamos que se haya generado correctamente
		assertNotNull(token);
		
		//verificamos que no este vacío
		assertFalse(token.isBlank());
	}
	
	@Test
	void extractUsername_ShouldReturnCorrectUsername() {
		
	}
	
	@Test
	void extractRoles_ShouldReturnSingleRole() {
		
	}
	
	@Test
	void extractRoles_ShouldReturnMultipleRoles() {
		
	}
	
	@Test
	void isTokenExpired_ShouldReturnFalse_WhenTokenIsValid() {
		
	}
	
	@Test
	void validateToken_ShouldReturnTrue_WhenTokenIsValid() {
		
	}
	
	@Test
	void validateToken_ShouldReturnFalse_WhenUsernameDoesNotMatch() {
		
	}
	
	@Test
	void validateToken_ShouldReturnFalse_WhenRolesDoNotMatch() {
		
	}
	
	
	
	@Test
	void isTokenExpired_ShouldReturnTrue_WhenTokenIsExpired() {
		
	}
	
	@Test
	void validateToken_ShouldReturnFalse_WhenTokenIsExpired() {
		
	}
	
	@Test
	void extractUsername_ShouldThrowMalformedJwtException() {
		
	}
	
	@Test
	void extractUsername_ShouldThrowSignatureException() {
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
