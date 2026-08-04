package com.la.northwind_java.security.services;

import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import com.la.northwind_java.repositories.UserRepository;
import com.la.northwind_java.security.models.User;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;
	
	
	public UserDetailsServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username)
				.orElseThrow(()-> new UsernameNotFoundException("User not found"));
		
		List<GrantedAuthority> authorities = user.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority("ROLE_"+ role.getName()))
				.collect(Collectors.toList());
		
		return new org.springframework.security.core.userdetails.User(
				user.getUsername(),
				user.getPassword(),
				authorities);
	}
	
	
}
