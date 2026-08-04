
package com.la.northwind_java.security.utils;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.la.northwind_java.security.models.User;
import com.la.northwind_java.security.services.UserDetailsServiceImpl;





public class SecurityUtils {
	
	
	public static void validateAccessToCustomer(Long requestedCustomerId,
											Authentication authentication) {
		User user = getUser(authentication);

		if(hasRole(user, "ROLE_ADMIN") || hasRole(user, "ROLE_EMPLOYEE")) return;
		
		if(hasRole(user, "ROLE_CUSTOMER") && user.getId().equals(requestedCustomerId))return;
		
		throw new AccessDeniedException("No tienes permiso para acceder a este cliente.");
	}
	
	
	
	public static void validateAccessToEmployee(Long requestedEmployeeId,
												Authentication authentication) {
		User user = getUser(authentication);
		if(hasRole(user,"ROLE_ADMIN")) return;
		if(hasRole(user, "ROLE_EMPLOYEE") && user.getId().equals(requestedEmployeeId))return;
		throw new AccessDeniedException("No tienes permiso para acceder a este empleado.");
	}
	
	
	public static void validateAccessToOrderCustomer(Long requestedEmployeeId,
												Authentication authentication) {
		User user = getUser(authentication);
		if(hasRole(user, "ROLE_ADMIN") || hasRole(user, "ROLE_EMPLOYEE")) return;
		if(hasRole(user, "ROLE_CUSTOMER") && user.getId().equals(requestedEmployeeId))return;
		throw new AccessDeniedException("No tienes permiso para acceder a esta orden.");
	}
	
	
	private static User getUser(Authentication authentication) {
		Object principal = authentication.getPrincipal();
		
		if(!(principal instanceof User)) {
			throw new AccessDeniedException("Authenticatión inválida.");
		}
		return (User) principal;
	}
	
	
	private static boolean hasRole(User user, String role) {
		for(GrantedAuthority authority : user.getAuthorities()) {
			if(authority.getAuthority().equals(role)) {
				return true;
			}
		}
		return false;
	}
	

}
