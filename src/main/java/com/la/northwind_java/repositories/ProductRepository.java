package com.la.northwind_java.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.la.northwind_java.models.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

}
