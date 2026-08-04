
package com.la.northwind_java.security.jwt;


import io.jsonwebtoken.*;
import java.util.Arrays;
import io.jsonwebtoken.security.Keys;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;


@Component
public class JWTUtils {

	private static final String SECRET_KEY = "3D1qKjfP9hOqpXn3tHfs+7YX4Q9z6IlrZaM7vM3XNkA";
	private static final long EXPIRATION_TIME = 86400000; //24 horas
	
	
	//Permite sobrescribir la expiración en los tests si fuera necesario
	protected long getExpirationTime() {
		return EXPIRATION_TIME;
	}
	//Permite sobreescribir la clave en los tests si fuera necesario
	protected SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
	}
	
	
	public String generateToken(UserDetails userDetails) {
		
		String roles = userDetails.getAuthorities()
				.stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(","));
		
		return Jwts.builder()
				.subject(userDetails.getUsername())
				.claim("roles", roles)//agregamos roles al token
				.issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + getExpirationTime()))
				.signWith(getSigningKey())
				.compact();
	}
	public boolean validateToken(String token, UserDetails userDetails) {
		
		final String username = extractUsername(token);
		
		final List<String> tokenRoles = extractRoles(token);
		
		final List<String> userRoles = userDetails.getAuthorities()
				.stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.toList());
		
		return username.equals(userDetails.getUsername()) 
				&& !isTokenExpired(token)
				&& userRoles.containsAll(tokenRoles);//comparamos los roles
	}
	
	public String extractUsername(String token) {
		return extractAllClaims(token).getSubject();
	}
	
	public List<String> extractRoles(String token){
		String roles = extractAllClaims(token)
							.get("roles", String.class);
		return Arrays.asList(roles.split(","));
	}
	
	public boolean isTokenExpired(String token) {
		return extractAllClaims(token)
				.getExpiration()
				.before(new Date());
	}
	private Claims extractAllClaims(String token) {
		return Jwts.parser()
				.verifyWith(getSigningKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}
}
