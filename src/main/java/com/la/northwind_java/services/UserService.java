package com.la.northwind_java.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.la.northwind_java.dtos.user.UserDTO;

public interface UserService {
	Page<UserDTO> getAllUsers(Pageable pageable);
	UserDTO getUserById(Long id);
	UserDTO linkEmployee(Long userId, Integer employeeId);

}
