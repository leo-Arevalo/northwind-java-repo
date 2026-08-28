package com.la.northwind_java.mappers;

import java.util.stream.Collectors;

import org.mapstruct.Mapper;

import com.la.northwind_java.dtos.user.UserDTO;
import com.la.northwind_java.models.Privileges;
import com.la.northwind_java.security.models.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

	default UserDTO toUserDTO(User user) {
		if(user == null) return null;
		
		UserDTO.UserDTOBuilder builder = UserDTO.builder()
				.id(user.getId())
				.username(user.getUsername())
				.email(user.getEmail())
				.enabled(user.isEnabled())
				.roles(user.getRoles().stream()
						.map(role -> role.getName())
						.collect(Collectors.toSet()));
		if(user.getEmployee() != null) {
			builder.employeeId(user.getEmployee().getId())
				.employeeFullName(
						(user.getEmployee().getFirstName() != null ? user.getEmployee().getFirstName() : "")
						+" "+
						(user.getEmployee().getLastName() != null ? user.getEmployee().getLastName() : "")
						)
				.privileges(user.getEmployee().getPrivileges().stream()
						.map(p-> p.getPrivilegeName())
						.collect(Collectors.toSet()));
			
		}
		return builder.build();
	}
}
