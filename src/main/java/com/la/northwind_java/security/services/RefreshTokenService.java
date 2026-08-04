

package com.la.northwind_java.security.services;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.la.northwind_java.repositories.UserRepository;
import com.la.northwind_java.security.models.RefreshToken;
import com.la.northwind_java.security.models.User;
import com.la.northwind_java.security.repositories.RefreshTokenRepository;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

	//valor que tomamos de aplication properties, el tiempo de vida del refresh token (por ejemplo 7 dias)
	@Value("${jwt.refreshExpirationMs}")
	private Long refreshTokenDurationMs;
	@Autowired
	private final RefreshTokenRepository refreshTokenRepository;
	@Autowired
	private final UserRepository userRepository;
	
	/**
	 *Crea un refresh token nuevo para un usuario dado por su nombre de usuario
	 * @param username
	 * @return
	 */
	public  RefreshToken createRefreshToken(String username) {
		//buscamos el usuario por su username
		User user = userRepository.findByUsername(username)
					.orElseThrow(() -> new RuntimeException("User not found"));
		
		//Borramos tokens previos(si queremos tener un solo token por usuario)
		refreshTokenRepository.deleteByUser(user);
		
		//creamos el refresh token
		RefreshToken refreshToken = RefreshToken.builder()
									.user(user) // lo asociamos al usuario
									.token(UUID.randomUUID().toString())//generamos el token unico
									.expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))// fecha de expiracion
									.build();
		//lo guardamos en la bd
		return refreshTokenRepository.save(refreshToken);
		
	}
	/**
	 * Verifica si un token ya expiró. Si está vencido, lo elimina.
	 
	 */
	@Transactional
	public RefreshToken verifyExpiration(RefreshToken token) {
		//compara la fecha actual con la fecha de expiración
		if(token.getExpiryDate().isBefore(Instant.now())) {
			refreshTokenRepository.delete(token);//si expiró, lo borramos
			throw new RuntimeException("Token expired. Please sign in again.");
		}
		return token; //si no expiró lo devolvemos
	}
	
	
	/**
	 * Busca refresh token por su valor
	 */
	
	public Optional<RefreshToken> findByToken(String token){
		return refreshTokenRepository.findByToken(token);
	}
	/**
	 * Borra todos los refresh tokens asociados a un usuario (por logout, por ejemplo)
	 * @param userId
	 * @return
	 */
	@Transactional
	public int deleteByUserId(Long userId) {
		User user = userRepository.findById(userId)
					.orElseThrow(() -> new IllegalArgumentException("User not found"));
		return refreshTokenRepository.deleteByUserId(userId);
	}
	
	
	
	
}
