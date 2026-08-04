
package com.la.northwind_java.services;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.la.northwind_java.dtos.supplier.SupplierCreateDTO;
import com.la.northwind_java.dtos.supplier.SupplierDTO;
import com.la.northwind_java.dtos.supplier.SupplierSearchDTO;
import com.la.northwind_java.dtos.supplier.SupplierUpdateDTO;
import com.la.northwind_java.models.Supplier;

public interface SupplierService {
	SupplierDTO createSupplier(SupplierCreateDTO supplierCreateDTO);
	SupplierDTO getSupplierById(Integer id);
	Page<SupplierDTO> getSuppliers(SupplierSearchDTO supplier, Pageable pageable);
	SupplierDTO updateSupplier(Integer id, SupplierUpdateDTO supplierUpdateDTO);
	void deleteSupplier(Integer id);
	List<SupplierDTO> findRecentSuppliers();
	List<Supplier> searchSuppliers(SupplierSearchDTO filters);
}
