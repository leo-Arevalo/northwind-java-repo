


package com.la.northwind_java.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;


import com.la.northwind_java.security.services.UserDetailsServiceImpl;
import com.la.northwind_java.security.jwt.JwtAuthenticationFilter;
import java.util.List;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.la.northwind_java.security.jwt.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	private final UserDetailsServiceImpl userDetailsService;
	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	
	public SecurityConfig(UserDetailsServiceImpl userDetailsService,
						JwtAuthenticationFilter jwtAuthenticationFilter) {
		this.userDetailsService = userDetailsService;
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	}
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		http.csrf(csrf -> csrf.disable())
		.cors(cors -> cors.configurationSource(corsConfigurationSource()))
		.authorizeHttpRequests(auth->auth
				.requestMatchers("/api/login","/api/refresh","/api/logout").permitAll()
				.requestMatchers("swagger-ui/**", "v3/api-docs/**").permitAll() //swagger accesible sin token http://localhost:8080/swagger-ui.html
				.requestMatchers("/orders/**").hasAuthority("ROLE_ADMIN")
				.requestMatchers("/customers/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_EMPLOYEE")
				.requestMatchers("/suppliers/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_EMPLOYEE")
				.anyRequest().authenticated()
				)
				.authenticationProvider(authenticationProvider())
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
		
		return http.build();
	}
	
	
	
	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration(); //Define qué origen puede acceder a la API (tu frontEnd)
		
		configuration.setAllowedOrigins(List.of("http://127.0.0.1:3000", "http://localhost:3000")); //cambiar segun corresponda al frontend
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); //metodos permitidos
		configuration.setAllowedHeaders(List.of("Authorization", "Content-Type")); //Headers que puede usar e frontend
		configuration.setAllowCredentials(true); //Permite enviar cookies/token en la cabecera
		
		//Aplica esta confiracion en todas las rutas
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration); //se aplica a todas las rutas
		return source;
		
		
		
		
	}
	
	
}






