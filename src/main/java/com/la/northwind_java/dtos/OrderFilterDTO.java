package com.la.northwind_java.dtos;

import java.util.Optional;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class OrderFilterDTO {
	@Min(0)
	private int page = 0;
	
	@Min(1)
	private int size = 10;
	
	private Optional<Integer> customerId = Optional.empty();
	private Optional<Integer> employeeId = Optional.empty();
	private Optional<String> status = Optional.empty();
}
