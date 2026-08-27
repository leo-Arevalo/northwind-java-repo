package com.la.northwind_java.services.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.la.northwind_java.config.exceptions.ResourceNotFoundException;
import com.la.northwind_java.dtos.user.UserDTO;
import com.la.northwind_java.mappers.UserMapper;
import com.la.northwind_java.models.Employee;
import com.la.northwind_java.repositories.EmployeeRepository;
import com.la.northwind_java.repositories.UserRepository;
import com.la.northwind_java.security.models.User;
import com.la.northwind_java.services.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	
	private final UserRepository userRepository;
	private final EmployeeRepository employeeRepository;
	private final UserMapper userMapper;
	
	@Override
	public Page<UserDTO> getAllUsers(Pageable pageable) {
		return userRepository.findAll(pageable).map(userMapper::toUserDTO);
	}

	@Override
	public UserDTO getUserById(Long id) {
		return userRepository.findById(id)
				.map(userMapper::toUserDTO)
				.orElseThrow(()-> new ResourceNotFoundException("Usuario no encontrado con id: "+ id));
	}

	/**
	 * VIncula (o desvincula, si employeeId es null) un Employee a un User.
	 * A partir de esto, User.getAuthorities() va a incluir tambien los
	 * privilegios de ese empleado la proxima vez que el usuario se
	 * loguee (los authorities quedan fijados en el JWT/sesion del login
	 * vigente, no se actualizan en caliente sobre un token ya emitido).
	 */
	
	
	@Override
	public UserDTO linkEmployee(Long userId, Integer employeeId) {
		User user = userRepository.findById(userId)
					.orElseThrow(()-> new ResourceNotFoundException("Usuario no encontrado con id: "+userId));
		if(employeeId == null) {
			user.setEmployee(null);
		}else {
			Employee employee = employeeRepository.findById(employeeId)
				.orElseThrow(()-> new ResourceNotFoundException("Empleado no encontrado con id: "+ employeeId));
			user.setEmployee(employee);
		}
		return userMapper.toUserDTO(userRepository.save(user));
	}

	
}
