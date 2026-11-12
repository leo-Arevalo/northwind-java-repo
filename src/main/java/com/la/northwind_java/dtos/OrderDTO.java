
package com.la.northwind_java.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.la.northwind_java.dtos.customer.CustomerDTO;
import com.la.northwind_java.dtos.payment.PaymentDetailsDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for transferring Order data
 * 
 * @author LeO
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Getter
@Setter

public class OrderDTO {

	private Integer orderID;
	private CustomerDTO customer;
	private EmployeeDTO employee;
	private LocalDateTime orderDate;
	private LocalDateTime shippedDate;
	private ShipperDTO shipper;
	private String shipName;
	private String shipAddress;
	private String shipCity;
	private String shipStateProvince;
	private String shipPostalCode;
	private String shipCountryRegion;
	private BigDecimal shippingFee;
	private BigDecimal taxes;
	private String paymentType;
	private LocalDateTime paidDate;
	
	//Pagos de la factura (vacio en pedidos viejos que no los registraron)
	private List<PaymentDetailsDTO> payments;
	private String notes;
	private Double taxRate;
	private OrderTaxStatusDTO taxStatus;
	private OrderStatusDTO status;
	private List<OrderDetailsDTO> orderDetails;
	
	
}
