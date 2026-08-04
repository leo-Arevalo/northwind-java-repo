package com.la.northwind_java.security.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.la.northwind_java.security.models.RefreshToken;
import com.la.northwind_java.security.models.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>{
	Optional<RefreshToken> findByToken(String token);
	@Transactional
	void deleteByUser(User user);
	@Transactional
	int deleteByUserId(Long userId);
}
