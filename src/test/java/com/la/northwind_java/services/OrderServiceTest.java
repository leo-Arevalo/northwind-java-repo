package com.la.northwind_java.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.la.northwind_java.controllers.OrderController;
import com.la.northwind_java.dtos.OrderDTO;
import com.la.northwind_java.dtos.OrderUpdateDTO;




@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

	@Mock
	private OrderService orderService;
	
	@InjectMocks
	private OrderController orderController;
	
	private OrderDTO orderDTO;
	
	
	
	
	@BeforeEach
	void setUp() {
		orderDTO = new OrderDTO();
		orderDTO.setOrderID(1);
	}
	
	@Test
	void testGetAllOrders() {
		Page<OrderDTO> orders = new PageImpl<>(List.of(orderDTO));
		when(orderService.getAllOrders(0, 10, null, null, null, null, null)).thenReturn(orders);
		Page<OrderDTO> result = orderService.getAllOrders(0, 1, null, null, null, null, null);
		assertThat(result.getContent()).hasSize(1).contains(orderDTO);
		
	}
	
	@Test
	void testCreateOrder() {
		when(orderService.createOrder(any(OrderDTO.class))).thenReturn(orderDTO);
		OrderDTO result = orderService.createOrder(orderDTO);
		assertThat(result).isEqualTo(orderDTO);
	}
	
	
	@Test
	void testGetOrderById() {
		when(orderService.getOrderById(1)).thenReturn(orderDTO);
		OrderDTO result = orderService.getOrderById(1);
		assertThat(result).isEqualTo(orderDTO);
	}
	@Test
	void testUpdateOrder() {
		OrderUpdateDTO updateDTO = new OrderUpdateDTO();
		when(orderService.updateOrder(eq(1), any(OrderUpdateDTO.class))).thenReturn(orderDTO);
		OrderDTO result = orderService.updateOrder(1, updateDTO);
		assertThat(result).isEqualTo(orderDTO);
	}
	
	@Test
	void testDeleteOrder() {
		doNothing().when(orderService).deleteOrder(1);
		orderService.deleteOrder(1);
		verify(orderService, times(1)).deleteOrder(1);
	}
	
	
	
	
	
	
	
}
