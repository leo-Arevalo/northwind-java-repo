package com.la.northwind_java.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.la.northwind_java.dtos.purchaseOrder.PurchaseOrderCreateDTO;
import com.la.northwind_java.dtos.purchaseOrder.PurchaseOrderDTO;
import com.la.northwind_java.dtos.purchaseOrder.PurchaseOrderUpdateDTO;
import com.la.northwind_java.dtos.purchaseOrder.PurchaseOrderSearchDTO;
public interface PurchaseOrderService {

	public Page<PurchaseOrderDTO> getOrders(PurchaseOrderSearchDTO filters, Pageable pageable);
	public List<PurchaseOrderDTO> findRecentOrders();
	public PurchaseOrderDTO getById(Integer id);
	public List<PurchaseOrderDTO> getBySupplierId(Integer supplierId);
	public PurchaseOrderDTO create(PurchaseOrderCreateDTO dto);
	public PurchaseOrderDTO update(Integer id, PurchaseOrderUpdateDTO dto);
	public void delete(Integer id);
	public List<PurchaseOrderDTO> getPendingOrders();
	public Double getTotalPaidToSupplier(Integer supplierId);
	public List<PurchaseOrderDTO> getByStatus(String status);
}
