package com.la.northwind_java.services;



import org.springframework.data.domain.Page;

import com.la.northwind_java.dtos.OrderDTO;
import com.la.northwind_java.dtos.OrderFilterDTO;
import com.la.northwind_java.dtos.OrderUpdateDTO;



public interface OrderService {
	
	public Page<OrderDTO> getAllOrders(int page, int size, Integer customerId, Integer employeeId, String status, String sortBy, String sortDirection);
	public OrderDTO getOrderById(Integer id);
	public OrderDTO createOrder(OrderDTO orderDTO);
	public OrderDTO updateOrder(Integer id, OrderUpdateDTO orderUpdateDTO);
	public void deleteOrder(Integer id);
}
