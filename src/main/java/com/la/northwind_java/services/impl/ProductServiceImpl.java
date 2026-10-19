package com.la.northwind_java.services.impl;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.la.northwind_java.config.exceptions.DatabaseException;
import com.la.northwind_java.config.exceptions.ResourceNotFoundException;
import com.la.northwind_java.dtos.product.ProductCreateDTO;
import com.la.northwind_java.dtos.product.ProductDTO;
import com.la.northwind_java.dtos.product.ProductStockDTO;
import com.la.northwind_java.dtos.product.ProductUpdateDTO;
import com.la.northwind_java.mappers.ProductMapper;
import com.la.northwind_java.models.Product;
import com.la.northwind_java.repositories.InventoryTransactionRepository;
import com.la.northwind_java.repositories.ProductRepository;

import com.la.northwind_java.services.ProductService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
	
	
	private final ProductRepository productRepository;
	
	private final ProductMapper productMapper;
	
	private final InventoryTransactionRepository inventoryTransactionRepository;
	/*
	 * Creates a new product.
	 */
	@Override
	public ProductDTO createProduct(ProductCreateDTO productCreateDTO) {
		try {
			Product product = productMapper.toProduct(productCreateDTO);
			product = productRepository.save(product);
			return productMapper.toProductDTO(product);
		}catch(DataAccessException e) {
			throw new DatabaseException("Error creating product", e);
		}

	}
	
	/*
	 * Retrieves a product by ID.
	 */
	@Override
	public ProductDTO getPRoductById(Integer id) {
		return productRepository.findById(id)
				.map(productMapper::toProductDTO)
				.orElseThrow(() -> new ResourceNotFoundException("Product not found with id "+id));

	}
	
	/*
	 * Retrieves paginated products.
	 */
	@Override
	@Cacheable(value = "products", key = "#pageable")
	public Page<ProductDTO> getAllProducts(Pageable pageable) {
		Page<Product> products = productRepository.findAll(pageable);
		return products.map(productMapper::toProductDTO);
		
	}

	/*
	 * Updates an existing product.
	 */
	
	@Override
	@Transactional
	public ProductDTO updateProduct(Integer id, ProductUpdateDTO productUpdateDTO) {
		
		Product product = productRepository.findById(id)
				.orElseThrow(()-> new ResourceNotFoundException("Product not found with id "+id));
		try {
			productMapper.updateEntity(productUpdateDTO, product);
			Product updateProduct = productRepository.save(product);
			return productMapper.toProductDTO(updateProduct);
		}catch(DataAccessException e) {
			throw new DatabaseException("Error updating product with id"+id, e);
		}

	}

	/*
	 * Deletes a product.
	 */
	@Override
	@Transactional
	public void deleteProduct(Integer id) {
		if(!productRepository.existsById(id)) {
			throw new ResourceNotFoundException("Product not found with id:" +id);
		}
		productRepository.deleteById(id);
	}

	/*
	 * Productos que un proveedor puede ofrecer. supplier_id es texto
	 * separado por coma en la base de datos( no una tabla intermedia),
	 * asi que se filtra en memoria en vez de armar un query SQL fragil con LIKE
	 * (que confundiria proveedor 1 con 10, 11, etc.)
	 */
	@Override
	public List<ProductDTO> getProductsBySupplier(Integer supplierId){
		return productRepository.findAll().stream()
				.filter(p -> p.getSupplierIdList().contains(supplierId))
				.map(productMapper::toProductDTO).toList();
	}
	@Override
	public ProductStockDTO getAvailableStock(Integer productId) {
		if(!productRepository.existsById(productId)) {
			throw new ResourceNotFoundException("Product not found with id: "+productId);
		}
		Long stock = inventoryTransactionRepository.getAvailableStock(productId);
		return ProductStockDTO.builder()
				.productoId(productId)
				.availableStock(stock)
				.build();
	}
	
	
	
}
