package com.la.northwind_java.security.jwt;


import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.la.northwind_java.security.jwt.JWTUtils;

import jakarta.servlet.FilterChain;

@ExtendWith(MockitoExtension.class) //Activa mockito para que procese @Mock y @InjectMocks 
public class JwtAuthenticationFilterTest { //clase de test del filtro JWT
	
	@Mock
	private JWTUtils jwtUtils; //mock del componente que parsea y valida tokens
	
	@Mock
	private UserDetailsService userDetailsService; // mock del servicio que carga usuarios desde BD
	
	@InjectMocks
	private JwtAuthenticationFilter filter; //instancia real del filtro con mocks inyectados
	
	
	private MockHttpServletRequest request; //request fake que simula llamada http
	private MockHttpServletResponse response; //response fake
	private FilterChain chain; //mock del chain de filtros
	
	@BeforeEach
	void setup() { //método que se ejecuta antes de cada test
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		chain = mock(FilterChain.class); //crea mock del chain
		
		SecurityContextHolder.clearContext(); // limpia autenticatión previa del contexto global
	
	}
	
	@Test
	void shouldAuthenticateWhenTokenIsValid() throws Exception { // test caso token válido
		
		String token = "validToken"; //token fake
		String username = "leo"; //username fake
		
		request.addHeader("Authorization", "Bearer"+ token); //agrega header Authorization a request
		
		UserDetails userDetails = new org.springframework.security.core.userdetails.User( //crea usuario security fake
				username,
				"password", //irrelevante en este test
				List.of(new SimpleGrantedAuthority("ROLE_ADMIN")) //roles del usuario
				);
		when(jwtUtils.extractUsername(token)).thenReturn(username); //mockea extracción username del token
		when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails); //mockea carga usuario
		when(jwtUtils.validateToken(token, userDetails)).thenReturn(true); //mockea validación correcta del token
		
		filter.doFilterInternal(request, response, chain); //ejecuta filtro con token válido
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication(); //obtiene autenticación creada por filtro
		assertNotNull(auth); //verifica que ahora hay authenticacion
		assertEquals(username, auth.getName()); //verifica que el username autenticado sea el correcto
		
		verify(chain).doFilter(request, response); // verifica que el chain continúa normalmente
		
	}
	
	@Test
	void shouldNotAuthenticateWhenTokenInvalid() throws Exception { //test caso token invalido
		String token = "invalidToken"; //token fake inválido
		String username = "leo"; //username fake
		 request.addHeader("Authentication", "Bearer " + token); //agrega header Authorization
		 
		 UserDetails userDetails = new org.springframework.security.core.userdetails.User(
				 username, 
				 "password", 
				 List.of()); //sin roles (irrelevante)
		
		 when(jwtUtils.extractUsername(token)).thenReturn(username); //mockea extraccion username
		 when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails); //mockea carga usuario
		 when(jwtUtils.validateToken(token, userDetails)).thenReturn(false); //mockea token inválido
		 
		 filter.doFilterInternal(request, response, chain); //ejecuta filtro
		 
		 Authentication auth = SecurityContextHolder.getContext().getAuthentication(); //obtiene authenticacion
		 assertNull(auth); //verifica que NO se autentico usuario
		 verify(chain).doFilter(request, response); //verifica que request continua su flujo
		 
		 
		 
	}

	
	
	
	
	
}


