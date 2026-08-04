

package com.la.northwind_java.dtos;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for OrderDetails
 * @author LeO
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class OrderDetailsDTO {
	private Integer orderId;
	private Integer productId;
	private Integer quantity;
	private BigDecimal unitPrice;
	private Double discount;
}
