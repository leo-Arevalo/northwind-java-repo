

package com.la.northwind_java.mappers;

import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import com.la.northwind_java.dtos.OrderDTO;
import com.la.northwind_java.dtos.OrderDetailsDTO;
import com.la.northwind_java.dtos.OrderStatusDTO;
import com.la.northwind_java.dtos.OrderTaxStatusDTO;
import com.la.northwind_java.dtos.OrderUpdateDTO;
import com.la.northwind_java.dtos.ShipperDTO;
import com.la.northwind_java.models.Order;
import com.la.northwind_java.models.OrderDetails;
import com.la.northwind_java.models.OrderStatus;
import com.la.northwind_java.models.OrderTaxStatus;
import com.la.northwind_java.models.Shipper;

/**
 * Mapper interface for converting between Order entities and DTOs.
 * Uses MapStruct for automatic mapping.
 * 
 * @author LeO
 *
 */

@Mapper(componentModel = "spring")
public interface OrderMapper {

	/**
	 * Converts an Order entity to an OrderDTO.
	 * Maps nested objects to their corresponding DTO representations.
	 */
			//model						DTO
	@Mapping(source = "customer", target = "customer")
	@Mapping(source = "employee", target = "employee")
	@Mapping(source = "orderDate", target = "orderDate")
	@Mapping(source = "shippedDate", target = "shippedDate")
	@Mapping(source = "shipper", target = "shipper", qualifiedByName = "mapShipper")
	@Mapping(source = "shipName", target = "shipName")
	@Mapping(source = "shipAddress", target = "shipAddress")
	@Mapping(source = "shipCity", target = "shipCity")
	@Mapping(source = "shipStateProvince", target = "shipStateProvince")
	@Mapping(source = "shipZipPostalCode", target = "shipPostalCode")
	@Mapping(source = "shipCountryRegion", target = "shipCountryRegion")
	@Mapping(source = "shippingFee", target = "shippingFee")
	@Mapping(source = "taxes", target = "taxes")
	@Mapping(source = "paymentType", target = "paymentType")
	@Mapping(source = "paidDate", target = "paidDate")
	@Mapping(source = "notes", target = "notes")
	@Mapping(source = "taxRate", target = "taxRate")
	@Mapping(source = "taxStatus", target = "taxStatus", qualifiedByName = "mapOrderTaxStatus")
	@Mapping(source = "status", target = "status", qualifiedByName = "mapOrderStatus")
	@Mapping(source = "orderDetails", target = "orderDetails", qualifiedByName = "mapOrderDetailsList")
	OrderDTO toOrderDTO(Order order);
	
	
	/**Converts an OrderDTO back to an Order entity.
	 * Uses the inverse of the mappings defined in toOrderDTO()
	 */
	
	@InheritInverseConfiguration
	@Mapping(source = "shipper", target = "shipper", qualifiedByName = "mapShipperEntity")
    @Mapping(source = "status", target = "status", qualifiedByName = "mapOrderStatusEntity")
	@Mapping(source = "taxStatus", target = "taxStatus", qualifiedByName = "mapOrderTaxStatusEntity")
	@Mapping(source = "orderDetails", target ="orderDetails", qualifiedByName = "mapOrderDetailsEntityList")
	Order toOrder(OrderDTO orderDTO);
	
	
	/**
	 * Updates an existing Order entity with values from an OrderUpdateDTO.
	 * This does not replace associations unless explicitly provided.
	 */
	@Mapping(target = "customer", ignore = true) // Relationships are handled at the service level.
	@Mapping(target = "employee", ignore = true)
	@Mapping(target = "shipper", ignore = true)
	@Mapping(target = "status", ignore = true)
	@Mapping(target = "taxStatus", ignore = true)
	@Mapping(target = "orderDetails", ignore = true)
	void updateEntity(OrderUpdateDTO orderUpdateDTO, @MappingTarget Order order);
	
	/**
	 * Maps a list of OrderDetails entities to a list of OrderDetailsDTO
	 * Uses a helper method to map each individual OrderDetails object.
	 */
	
	@Named("mapOrderDetailsList")
	default List<OrderDetailsDTO> mapOrderDetailsList(List<OrderDetails> orderDetails) {
	    return orderDetails != null ? orderDetails.stream().map(this::toOrderDetailsDTO).toList() : null;
	}
	
	
	@Named("mapOrderDetailsEntityList")
	default List<OrderDetails> mapOrderDetailsEntityList(List<OrderDetailsDTO> orderDetailsDTOs){
		return orderDetailsDTOs != null ? orderDetailsDTOs.stream().map(this::toOrderDetails).toList() : null;
	}
	
	
	/**Converts an OrderDetails entity to an OrderDetailsDTO.
	 * Maps the product association to its corresponding ID.
	 */
	@Named("toOrderDetailsDTO")
	
	default OrderDetailsDTO toOrderDetailsDTO(OrderDetails orderDetails) {
		if(orderDetails == null) return null;
		return new OrderDetailsDTO(
				 orderDetails.getId(),
				 orderDetails.getOrder().getId(),
				 orderDetails.getProduct().getProductID(),
				 orderDetails.getUnitPrice(),
				 orderDetails.getDiscount()
				);
	}
	
	/**
	 * Converts an OrderDetailsDTO back to an OrderDetails entity.
	 * Uses the inverse of the mappings defined in toOrderDetailsDTO().
	 */
	
	@InheritInverseConfiguration
	OrderDetails toOrderDetails(OrderDetailsDTO orderDetailsDTO);
	
	/**
	 * Converts an OrderStatus entity to an OrderStatusDTO.
	 * @param orderStatus
	 * @return
	 */
	@Named("mapOrderStatus")
	default OrderStatusDTO toOrderStatusDTO(OrderStatus orderStatus) {
		if(orderStatus == null) return null;
		return new OrderStatusDTO(orderStatus.getId(), orderStatus.getStatusName());
	}
	
	/**
     * Converts an OrderStatusDTO back to an OrderStatus entity.
     */
    @Named("mapOrderStatusEntity")
    
    default OrderStatus toOrderStatusEntity(OrderStatusDTO orderStatusDTO) {
    	if(orderStatusDTO == null) return null;
    	return new OrderStatus(orderStatusDTO.getId(), orderStatusDTO.getStatusName());
    }

    /**
     * Converts an OrderTaxStatus entity to an OrderTaxStatusDTO.
     */
    @Named("mapOrderTaxStatus")
    default OrderTaxStatusDTO toOrderTaxStatusDTO(OrderTaxStatus taxStatus) {
    	if(taxStatus == null) return null;
    	return new OrderTaxStatusDTO(taxStatus.getId(), taxStatus.getTaxStatusName());
    }
	@Named("mapOrderTaxStatusEntity")
	default OrderTaxStatus toOrderTaxStatusEntity(OrderTaxStatusDTO taxStatusDTO) {
		if(taxStatusDTO == null) return null;
		return new OrderTaxStatus(taxStatusDTO.getId(), taxStatusDTO.getTaxStatusName());
	}
    
    
	/**
     * Converts a Shipper entity to a ShipperDTO.
     */
	@Named("mapShipper")
	default ShipperDTO mapShipper(Shipper shipper) {
	    if (shipper == null) return null;
	    return new ShipperDTO(shipper.getId(),
	    						shipper.getCompany(),
	    						shipper.getLastName(),
	    						shipper.getFirstName(),
	    						shipper.getEmail(),
	    						shipper.getJobTitle(),
	    						shipper.getBusinessPhone(),
	    						shipper.getHomePhone(),
	    						shipper.getMobilePhone(),
	    						shipper.getFaxNumber(),
	    						shipper.getAddress(),
	    						shipper.getCity(),
	    						shipper.getStateProvince(),
	    						shipper.getZipPostalCode(),
	    						shipper.getCountryRegion(),
	    						shipper.getWebPage(),
	    						shipper.getNotes(),
	    						shipper.getAttachments()
	    						);
	}
	 /**
     * Converts a ShipperDTO back to a Shipper entity.
     */
	@Named("mapShipperEntity")
	default Shipper mapShipperEntity(ShipperDTO shipperDTO) {
	    if (shipperDTO == null) return null;
	    return new Shipper(shipperDTO.getId(),
				shipperDTO.getCompany(),
				shipperDTO.getLastName(),
				shipperDTO.getFirstName(),
				shipperDTO.getEmail(),
				shipperDTO.getJobTitle(),
				shipperDTO.getBusinessPhone(),
				shipperDTO.getHomePhone(),
				shipperDTO.getMobilePhone(),
				shipperDTO.getFaxNumber(),
				shipperDTO.getAddress(),
				shipperDTO.getCity(),
				shipperDTO.getStateProvince(),
				shipperDTO.getZipPostalCode(),
				shipperDTO.getCountryRegion(),
				shipperDTO.getWebPage(),
				shipperDTO.getNotes(),
				shipperDTO.getAttachments(),
				
				null
				);
	}
	
	@AfterMapping
	default void normalizeData(@MappingTarget Order order) {
		if(order.getShipName() != null) {
			order.setShipName(order.getShipName().trim().toUpperCase());
		}
		if(order.getShipCity() != null) {
			order.setShipCity(order.getShipCity().trim());
		}
	}
	
	
}
