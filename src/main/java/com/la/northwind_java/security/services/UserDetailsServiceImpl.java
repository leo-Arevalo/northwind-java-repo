package com.la.northwind_java.security.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.la.northwind_java.repositories.UserRepository;
import com.la.northwind_java.security.models.User;



@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;
	
	
	public UserDetailsServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));
		//Los privilegios del Employee son LAZY: se recorren acá, con la sesion abierta,
		//porque el filtro JWT llama a getAuthorities() después, sin sesión de Hibernate.
		user.getAuthorities();
		return user;
	}
	
	
}
