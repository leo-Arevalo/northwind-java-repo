

package com.la.northwind_java.controllers;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;

import java.time.LocalDateTime;
import java.util.List;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.la.northwind_java.config.exceptions.ResourceNotFoundException;
import com.la.northwind_java.dtos.EmployeeDTO;
import com.la.northwind_java.dtos.OrderCreateDTO;
import com.la.northwind_java.dtos.OrderDTO;
import com.la.northwind_java.dtos.OrderUpdateDTO;
import com.la.northwind_java.dtos.customer.CustomerDTO;
import com.la.northwind_java.services.OrderService;



@WebMvcTest(OrderController.class)
public class OrderControllerTest {

	@Autowired
	private MockMvc mockMvc;//simula peticiones HTTP al controlador
	
	//cramos el mock del servicio para simular la lógica de negocio
	@MockBean
	private OrderService orderService;
	
	@Autowired
	private ObjectMapper objectMapper;//convierte el objeto java a JSON y viceversa
	
	private OrderDTO orderDTO;
	
	@BeforeEach
	void setUp() {
		CustomerDTO mockCustomer = mock(CustomerDTO.class);
		when(mockCustomer.getId()).thenReturn(123);
		
		EmployeeDTO mockEmployee = mock(EmployeeDTO.class);
		
		orderDTO = OrderDTO.builder()
				.orderID(1)
				.customer(mockCustomer)//usa el mock
				.employee(mockEmployee)//usa el mock
				//.customer(new CustomerDTO(123, "John Doe"))
				//.employee(new EmployeeDTO(1, "Jane Doe"))
				.orderDate(LocalDateTime.of(2024, 1, 1, 0, 0))
				.shippedDate(LocalDateTime.of(2024, 1, 5, 0, 0))
		.build();
	}
	/**
	 * Prueba la obtención de todas las ordenes con paginación y ordenamiento
	 * @throws Exception
	 */
	@Test
	void testGetAllOrders() throws Exception{
		Page<OrderDTO> orderPage = new PageImpl<>(List.of(orderDTO), PageRequest.of(0, 10, Sort.by("orderDate").ascending()),1);
		when(orderService.getAllOrders(0, 10, null, null, null, "orderDate", "asc")).thenReturn(orderPage);
		
		mockMvc.perform(get("/orders?page=0&size=10&sortBy=orderDate&sortDirection=asc")) // simula una petición GET a la API
		.andExpect(status().isOk()) // Espera un código HTTP 200 ok
		.andExpect(jsonPath("$.content[0].orderID").value(1)) // Verifica que el primer pedido tenga ID 1
		.andExpect(jsonPath("$.totalPages").value(1)); // Verifica que hay solo una página de resultados
	}
	
	/**
	 * Probamos cuando no hay ordenes en la base de datos
	 * @throws Exception
	 */
	@Test
	void testGetAllOrders_EmptyList() throws Exception{
		Page<OrderDTO> orderPage = new PageImpl<>(List.of(), PageRequest.of(0,  10, Sort.by("orderDate").ascending()), 0);
		when(orderService.getAllOrders(0, 10, null, null, null,"orderDate","asc")).thenReturn(orderPage);
		
		mockMvc.perform(get("/orders?page=0&size=10&sortBy=orderDate&sortDirection=asc"))//simula una petición GET con paginación
				.andExpect(status().isOk()) //espera 200 OK
				.andExpect(jsonPath("$.totalElements").value(0)); // verifica que no haya ordenes en la respuesta
	}
	
	
	/**
	 * Prueba la obtención de ordenes con filtros especificos
	 * @throws Exception
	 */
	
	@Test
	@WithMockUser(authorities = {"ROLE_ADMIN"})
	void testGetAllOrdersWithFilters() throws Exception{
		Page<OrderDTO> orderPage = new PageImpl<>(List.of(orderDTO), PageRequest.of(0, 10, Sort.by("orderDate").ascending()), 1);
		when(orderService.getAllOrders(0, 10, 123, null, null, "orderDate", "asc")).thenReturn(orderPage);
		
		mockMvc.perform(get("/orders")
				.param("page", "0")
				.param("size", "10")
				.param("customerId", "123")
				.param("sortBy", "orderDate")
				.param("sortDirection", "asc"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content[0].orderID").value(1))
		.andExpect(jsonPath("$.content[0].customer.id").value(123));
	}
	
	/**
	 * Prueba la obtención de una orden especifica por su id
	 * @throws Exception
	 */
	@Test
	void testGetOrderById() throws Exception{
		when(orderService.getOrderById(1)).thenReturn(orderDTO);
		mockMvc.perform(get("/orders/1")) //simula una petición GET para obtener una orden especifica
			.andExpect(status().isOk()) //espera 200 OK
			.andExpect(jsonPath("$.orderID").value(1)); //verifica que el ID de la orden sea 1
	}
	
	/**
	 * Prueba la obtención de una orden inexistente (debe devolver 404)
	 * @throws Exception
	 */
	@Test
	void testGetOrderById_NotFound() throws Exception {
		when(orderService.getOrderById(1)).thenThrow(new ResourceNotFoundException("Order not found"));
		mockMvc.perform(get("/orders/1"))
		.andExpect(status().isNotFound()) //espera un error 404
		.andExpect(jsonPath("$.message").value("Order not found")); //verifica el mensaje de error
	}
	
	/**
	 * Prueba la creación de una orden válida
	 * @throws Exception
	 */
	@Test
	void testCreateOrder() throws Exception {
		when(orderService.createOrder(Mockito.any(OrderDTO.class))).thenReturn(orderDTO);
		mockMvc.perform(post("/orders")
				.contentType(MediaType.APPLICATION_JSON) //indica que el contenido es JSON
				.content(objectMapper.writeValueAsString(orderDTO))) //Convierte la orden en JSON y la envia en el cuerpo
				.andExpect(status().isCreated()) //espera 201 created
				.andExpect(jsonPath("$.orderID").value(1)); //Verifica que el pedido creado tenga ID 1
		
		//Captura el objeto que se paso al método createOrder en el mock
		ArgumentCaptor<OrderDTO> orderCaptor = ArgumentCaptor.forClass(OrderDTO.class);
		verify(orderService).createOrder(orderCaptor.capture());//Verifica que el método fue llamado y captura el argumento
		assertThat(orderCaptor.getValue().getOrderID()).isEqualTo(1); //Asegura que el ID del pedido capturado es 1
		
	}
	@Test
	void testCreateOrder_InvalidData() throws Exception {
		mockMvc.perform(post("/orders")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{}"))
				.andExpect(status().isBadRequest());
	}
	/**
	 * Prueba la creacion de una orden con datos incompletos para validar 
	 * que devuelva un error 400 bad request
	 * @throws Exception
	 */
	
	@Test
	void testCreateOrder_MissingFields() throws Exception{
		OrderCreateDTO orderCreateDTO = new OrderCreateDTO();
		mockMvc.perform(post("/orders")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(orderCreateDTO)))
				.andExpect(status().isBadRequest()); //espera 400 bad request debido a datos incompletos
	}
	
	@Test
	void testUpdateOrder() throws Exception {
		OrderUpdateDTO updateDTO = new OrderUpdateDTO();
		when(orderService.updateOrder(eq(1), Mockito.any(OrderUpdateDTO.class))).thenReturn(orderDTO);
		mockMvc.perform(put("/orders/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDTO)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.orderID").value(1));
		ArgumentCaptor<OrderUpdateDTO> updateCaptor = ArgumentCaptor.forClass(OrderUpdateDTO.class);
		verify(orderService).updateOrder(eq(1), updateCaptor.capture());
		assertThat(updateCaptor.getValue()).isNotNull();
	}
	
	@Test
	void testUpdateOrder_NotFound() throws Exception {
	    OrderUpdateDTO updateDTO = new OrderUpdateDTO();
	    when(orderService.updateOrder(eq(1), Mockito.any(OrderUpdateDTO.class)))
	            .thenThrow(new ResourceNotFoundException("Order not found"));
	    
	    mockMvc.perform(put("/orders/1")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(updateDTO)))
	            .andExpect(status().isNotFound())
	            .andExpect(jsonPath("$.message").value("Order not found"));
	}
	
	/**
	 * Prueba la actualización de una orden con un shippingFee negativo,
	 * lo que debería fallar por validación.
	 * @throws Exception
	 */
	@Test
	void testUpdate_InvalidShippingFee() throws Exception {
		OrderUpdateDTO updateDTO = new OrderUpdateDTO();
		updateDTO.setShippingFee(BigDecimal.valueOf(-10.00));
		
		mockMvc.perform(put("/orders/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDTO)))
				.andExpect(status().isBadRequest());
	}
	
	@Test
	void testDeleteOrder() throws Exception {
		doNothing().when(orderService).deleteOrder(1);
		mockMvc.perform(delete("/orders/1"))
				.andExpect(status().isOk()); //se espera 200 ok
		verify(orderService, times(1)).deleteOrder(1);
	}
	
	@Test
	void testDeleteOrder_NotFound() throws Exception {
	    doThrow(new ResourceNotFoundException("Order not found")).when(orderService).deleteOrder(1);
	    mockMvc.perform(delete("/orders/1"))
	            .andExpect(status().isNotFound())
	            .andExpect(jsonPath("$.message").value("Order not found"));
	}
	/**
	 * Confirma que la eliminación de una orden exitosa devuelve 200 ok
	 * @throws Exception
	 */
	@Test
	void testDeleteOrder_Success() throws Exception{
		doNothing().when(orderService).deleteOrder(1);
		mockMvc.perform(delete("/orders/1"))
				.andExpect(status().isOk());
	}
	
	
}




