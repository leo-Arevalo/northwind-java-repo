
package com.la.northwind_java.controllers;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.la.northwind_java.dtos.supplier.SupplierCreateDTO;
import com.la.northwind_java.dtos.supplier.SupplierDTO;
import com.la.northwind_java.dtos.supplier.SupplierSearchDTO;
import com.la.northwind_java.services.SupplierService;

import com.la.northwind_java.config.exceptions.ResourceNotFoundException;
import com.la.northwind_java.dtos.supplier.*;
import org.mockito.Mockito;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SupplierController.class)
@AutoConfigureMockMvc
public class SupplierControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private SupplierService supplierService;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	
	
	
	@Test
	void testGetAllSuppliers_ok() throws Exception {
		SupplierDTO supplier = SupplierDTO.builder()
				.id(1L)
				.company("Tech SA")
				.firstName("Ana")
				.lastName("Lopez")
				.emailAddress("ana@tech.com")
				.build();
		when(supplierService.getSuppliers(any(SupplierSearchDTO.class), any()))
		.thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(supplier)));
		
		mockMvc.perform(get("/api/suppliers")
						.param("company", "Tech")
						.param("page", "0")
						.param("size", "5"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.content[0].company").value("Tech SA"));
		
	}
	
	@Test
	void testGetRecentSuppliers_ok() throws Exception {
		SupplierDTO supplier = SupplierDTO.builder()
				.id(1L)
				.company("Recent Co")
				.build();
		when(supplierService.findRecentSuppliers()).thenReturn(List.of(supplier));
		
		mockMvc.perform(get("/api/suppliers/recent"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].company").value("Recent Co"));
		
	}
	
	@Test
	void testGetSupplierById_notFound() throws Exception {
		when(supplierService.getSupplierById(1)).thenThrow(new ResourceNotFoundException("Supplier not found"));
		
		mockMvc.perform(get("api/suppliers/1"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Supplier not found"));
		
	}
	
	@Test
	void testCreateSupplier_badRequest() throws Exception {
		SupplierCreateDTO dto = SupplierCreateDTO.builder()
				.company("")
				.emailAddress("notanemail")
				.build();
		
		mockMvc.perform(post("/api/suppliers")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto)))
				.andExpect(status().isBadRequest());
	}
	
	
	 @Test
	    void testCreateSupplier_success() throws Exception {
	        SupplierCreateDTO dto = SupplierCreateDTO.builder()
	                .company("Create Co")
	                .firstName("John")
	                .lastName("Smith")
	                .emailAddress("john@create.com")
	                .build();

	        SupplierDTO response = SupplierDTO.builder()
	                .id(1L)
	                .company("Create Co")
	                .firstName("John")
	                .build();

	        when(supplierService.createSupplier(any())).thenReturn(response);

	        mockMvc.perform(post("/api/suppliers")
	                        .contentType(MediaType.APPLICATION_JSON)
	                        .content(objectMapper.writeValueAsString(dto)))
	                .andExpect(status().isCreated())
	                .andExpect(jsonPath("$.company").value("Create Co"));
	    }

	    @Test
	    void testUpdateSupplier_notFound() throws Exception {
	        SupplierUpdateDTO updateDTO = SupplierUpdateDTO.builder()
	                .id(99L)
	                .company("Updated Co")
	                .firstName("Updated")
	                .lastName("Name")
	                .emailAddress("updated@email.com")
	                .build();

	        when(supplierService.updateSupplier(eq(99), any())).thenThrow(new ResourceNotFoundException("Supplier not found"));

	        mockMvc.perform(put("/api/suppliers/99")
	                        .contentType(MediaType.APPLICATION_JSON)
	                        .content(objectMapper.writeValueAsString(updateDTO)))
	                .andExpect(status().isNotFound());
	    }

	    @Test
	    void testDeleteSupplier_success() throws Exception {
	        mockMvc.perform(delete("/api/suppliers/1"))
	                .andExpect(status().isNoContent());
	    }

	    @Test
	    void testDeleteSupplier_notFound() throws Exception {
	        doThrow(new ResourceNotFoundException("Not found")).when(supplierService).deleteSupplier(999);

	        mockMvc.perform(delete("/api/suppliers/999"))
	                .andExpect(status().isNotFound())
	                .andExpect(jsonPath("$.message").value("Not found"));
	    }
	
	
	
	
}





