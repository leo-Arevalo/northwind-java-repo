package com.la.northwind_java.security.services;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import com.la.northwind_java.repositories.UserRepository;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;
	
	
	public UserDetailsServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByUsername(username)
				.orElseThrow(()-> new UsernameNotFoundException("User not found"));
	}
	
	
}
