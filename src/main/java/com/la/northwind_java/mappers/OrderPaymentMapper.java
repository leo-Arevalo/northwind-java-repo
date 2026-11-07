package com.la.northwind_java.mappers;

import org.mapstruct.Mapper;

import com.la.northwind_java.models.OrderPayment;
import com.la.northwind_java.dtos.payment.PaymentDetailsDTO;
@Mapper(componentModel = "spring")
public interface OrderPaymentMapper {
	
	//Los nombres coinciden campo a campo; id y order de la entidad no se exponen.
	PaymentDetailsDTO toDTO(OrderPayment payment);

}
