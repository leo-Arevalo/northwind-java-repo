
package com.la.northwind_java.services.impl;



import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import com.la.northwind_java.config.exceptions.DatabaseException;
import com.la.northwind_java.dtos.OrderDTO;

import com.la.northwind_java.dtos.OrderUpdateDTO;
import com.la.northwind_java.mappers.OrderMapper;
import com.la.northwind_java.models.Order;
import com.la.northwind_java.repositories.OrderRepository;
import com.la.northwind_java.services.OrderService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

	private final OrderRepository orderRepository;
	private final OrderMapper orderMapper;
	
	@Override
	@Cacheable(value = "orders", key = "#page + '-' + #size + '-' + #customerId + '-' + #employeeId + '-' + #status + '-' + #sortBy + '-' + #sortDirection")
	public Page<OrderDTO> getAllOrders(int page, int size, Integer customerId, Integer employeeId, String status, String sortBy, String sortDirection){
		Sort sort = sortDirection.equalsIgnoreCase("desc")? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
		Pageable pageable = PageRequest.of(page, size, sort);
		Page<Order> orders = orderRepository.findOrders(pageable, customerId, employeeId, status);
		return orders.map(orderMapper::toOrderDTO);
		
	}
	
	@Override
	@Transactional
	public OrderDTO createOrder(OrderDTO orderDTO) {
		Order order = orderMapper.toOrder(orderDTO);
		return orderMapper.toOrderDTO(orderRepository.save(order));
	}
	@Override
	public OrderDTO getOrderById(Integer id) {
		return orderRepository.findById(id)
				.map(orderMapper::toOrderDTO)
				.orElseThrow(()->new ResourceNotFoundException("Order not found"));
	}
	@Override
	@Transactional
	
	public OrderDTO updateOrder(Integer id, OrderUpdateDTO orderUpdateDTO) {
		Order order = orderRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found with id: "+ id));
		
		
		try {
			orderMapper.updateEntity(orderUpdateDTO, order);
			Order updatedOrder = orderRepository.save(order);
			return orderMapper.toOrderDTO(updatedOrder);
		}catch(DataAccessException e) {
			throw new DatabaseException("Error updating order with id: " + id, e);
		}
		
	}
	
	
	@Override
	@Transactional
	public void deleteOrder(Integer id) {
		if(!orderRepository.existsById(id)) {
			throw new ResourceNotFoundException("Order not found");
		}
		orderRepository.deleteById(id);
	}
	
	
	
	
}













