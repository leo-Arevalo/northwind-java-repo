package com.la.northwind_java.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.la.northwind_java.dtos.product.ProductCreateDTO;
import com.la.northwind_java.dtos.product.ProductDTO;
import com.la.northwind_java.dtos.product.ProductUpdateDTO;
import com.la.northwind_java.models.Product;

/*
 * Mapper interface for converting between Product entities and DTOs.
 * Uses MapStruct for automatic mapping
 */

@Mapper(componentModel = "spring")
public interface ProductMapper {
	
	ProductDTO toProductDTO(Product product);
	
	/**
	 * Create a new Product entity with values from a ProductCreateDTO
	 */
	@Mapping(target = "productID", ignore = true) //Id se genera automaticamente
	@Mapping(target = "orderDetails", ignore = true) //no hay detalles de pedido al crear
	Product toProduct(ProductCreateDTO productCreateDTO);
	
	
	/**
	 * Updates an existing Product entity with values from a ProductUpdateDTO.
	 */
	@Mapping(target = "orderDetails", ignore = true) //no sobrescribir detalles de pedido
	void updateEntity(ProductUpdateDTO productUpdateDTO, @MappingTarget Product product);
	
	

}
