
package com.la.northwind_java.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.la.northwind_java.config.exceptions.ResourceNotFoundException;
import com.la.northwind_java.dtos.supplier.SupplierCreateDTO;
import com.la.northwind_java.dtos.supplier.SupplierDTO;
import com.la.northwind_java.dtos.supplier.SupplierSearchDTO;
import com.la.northwind_java.mappers.SupplierMapper;
import com.la.northwind_java.models.Supplier;
import com.la.northwind_java.repositories.SupplierRepository;
import com.la.northwind_java.services.impl.SupplierServiceImp;


import com.fasterxml.jackson.databind.ObjectMapper;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;




public class SupplierServiceImplTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private SupplierService supplierService;

	@Mock
	private SupplierRepository supplierRepository;
	
	private final SupplierMapper supplierMapper = Mappers.getMapper(SupplierMapper.class);
	
	@InjectMocks
	private SupplierServiceImp supplierServiceImpl;
	
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		supplierServiceImpl = new SupplierServiceImp(supplierRepository, supplierMapper);
	}
	
	@Test
	void testGetSupplierById_found() {
		Supplier supplier = Supplier.builder()
				.id(1)
				.company("Test Corp")
				.firstName("John")
				.lastName("Doe")
				.emailAddress("jhon@test.com")
				.build();
		
		when(supplierRepository.findById(1)).thenReturn(Optional.of(supplier));
		
		SupplierDTO result = supplierServiceImpl.getSupplierById(1);
		assertEquals("Test Corp", result.getCompany());
		assertEquals("John", result.getFirstName());
	}
	
	@Test
	void testGetSupplierById_notFound() {
		when(supplierRepository.findById(99)).thenReturn(Optional.empty());
		assertThrows(ResourceNotFoundException.class, () -> supplierServiceImpl.getSupplierById(99));
	}
	
	@Test
	void testFindRecentSuppliers() {
		
		Supplier supplier = Supplier.builder().id(1).company("Recent Corp").build();
		Page<Supplier> page = new PageImpl<>(List.of(supplier));
		
		when(supplierRepository.findAll(any(Pageable.class))).thenReturn(page);
		
		List<SupplierDTO> result = supplierServiceImpl.findRecentSuppliers();
		assertEquals(1, result.size());
		assertEquals("Recent Corp", result.get(0).getCompany());
	}
	
	@Test
	void testCreateSupplier_success() {
		SupplierCreateDTO dto = SupplierCreateDTO.builder()
				.company("New Corp")
				.firstName("Ana")
				.lastName("Smith")
				.emailAddress("ana@corp.com")
				.build();
		Supplier saved = supplierMapper.toSupplier(dto);
		saved.setId(100);
		
		when(supplierRepository.save(any(Supplier.class))).thenReturn(saved);
		
		SupplierDTO result = supplierServiceImpl.createSupplier(dto);
		assertNotNull(result.getId());
		assertEquals("New Corp", result.getCompany());
		
	}
	
	@Test
	void testDeleteSupplier_notFound() {
		//si no existe supplier.id = 999 -> false
		when(supplierRepository.existsById(999)).thenReturn(false);
		//el servicio debe lanzar una exception
		assertThrows(ResourceNotFoundException.class, () -> supplierServiceImpl.deleteSupplier(999));
	}
	
	/*
	 * Prueba llamar GET /api/suppliers con paginación y filtro company = Tech
	 * Verifica que la respuesta incluya un supplier esperado
	 */
	
	@Test
	void testGetSuppliers_withFilters() throws Exception {
		SupplierSearchDTO filters = new SupplierSearchDTO();
		filters.setCompany("Tech");
		
		SupplierDTO dto = SupplierDTO.builder()
				.id(1L)
				.company("Tech Solutions")
				.firstName("Jane")
				.lastName("Doe")
				.emailAddress("jane@tech.com")
				.build();
		
		Page<SupplierDTO> resultPage = new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1);
		when(supplierServiceImpl.getSuppliers(any(SupplierSearchDTO.class), any(Pageable.class))).thenReturn(resultPage);
		mockMvc.perform(get("/api/suppliers")
				.param("company", "Tech")
				.param("page", "0")
				.param("size", "10")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].company").value("Tech Solutions"));
		
	}
	
	/*
	 * Simula un POST con datos invalidos (@Valid fallando) y espera errores 400 bad request
	 */
	  	@Test
	    void testCreateSupplier_validationError() throws Exception {
	        SupplierCreateDTO invalidDto = SupplierCreateDTO.builder()
	                .company("")
	                .firstName("")
	                .lastName("")
	                .emailAddress("invalidemail")
	                .build();

	        mockMvc.perform(post("/api/suppliers")
	                        .contentType(MediaType.APPLICATION_JSON)
	                        .content(objectMapper.writeValueAsString(invalidDto)))
	                .andExpect(status().isBadRequest())
	                .andExpect(jsonPath("$.company").exists())
	                .andExpect(jsonPath("$.firstName").exists())
	                .andExpect(jsonPath("$.emailAddress").exists());
	    }
	  	
	  	/*
	  	 * prueba un GET /api/supplier/1000 con un ID inexistente y espera un error 404
	  	 * personalizado por GlobalExceptionHandler
	  	 */

	    @Test
	    void testGetSupplierById_notFound_controller() throws Exception {
	        when(supplierService.getSupplierById(1000)).thenThrow(new ResourceNotFoundException("Not found"));

	        mockMvc.perform(get("/api/suppliers/1000"))
	                .andExpect(status().isNotFound())
	                .andExpect(jsonPath("$.error").value("Resource not found"))
	                .andExpect(jsonPath("$.message").value("Not found"));
	    }
	
	
}






