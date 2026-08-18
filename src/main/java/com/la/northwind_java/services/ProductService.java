package com.la.northwind_java.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.la.northwind_java.dtos.product.ProductCreateDTO;
import com.la.northwind_java.dtos.product.ProductDTO;
import com.la.northwind_java.dtos.product.ProductUpdateDTO;

public interface ProductService {
	ProductDTO createProduct(ProductCreateDTO productCreateDTO);
	ProductDTO getPRoductById(Integer id);
	Page<ProductDTO> getAllProducts(Pageable pageable);
	ProductDTO updateProduct(Integer id, ProductUpdateDTO productUpdateDTO);
	void deleteProduct(Integer id);
}
