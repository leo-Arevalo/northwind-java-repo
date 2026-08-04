package com.la.northwind_java.specification;

import com.la.northwind_java.models.PurchaseOrder;
import com.la.northwind_java.models.PurchaseOrder;
import com.la.northwind_java.models.Supplier;
import com.la.northwind_java.models.PurchaseOrderStatus;
import com.la.northwind_java.dtos.purchaseOrder.PurchaseOrderSearchDTO;

import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public class PurchaseOrderSpecification {

	public static Specification<PurchaseOrder> withFilters(PurchaseOrderSearchDTO dto){
		return Specification
				.where(hasSupplierId(dto.getSupplierId()))
				.and(hasCreatedById(dto.getCreatedById()))
				.and(hasStatusName(dto.getStatusName()))
				.and(hasPaymentMethod(dto.getPaymentMethod()))
				.and(hasCreationDateBetween(dto.getFromCreationDate(), dto.getToCreationDate()))
				.and((hasPaymentAmountBetween(dto.getMinPayment(), dto.getMaxPayment())));
	}
	
	public static Specification<PurchaseOrder> hasSupplierId(Integer supplierId){
		return (root, query, cb) -> supplierId == null ? null:
			cb.equal(root.get("supplier").get("id"), supplierId);
	}
	
	public static Specification<PurchaseOrder> hasCreatedById(Integer employeeId) {
        return (root, query, cb) -> employeeId == null ? null :
                cb.equal(root.get("createdBy").get("id"), employeeId);
    }

    public static Specification<PurchaseOrder> hasStatusName(String statusName) {
        return (root, query, cb) -> statusName == null || statusName.isBlank() ? null :
                cb.equal(cb.upper(root.get("status").get("name")), statusName.toUpperCase());
    }

    public static Specification<PurchaseOrder> hasPaymentMethod(String paymentMethod) {
        return (root, query, cb) -> paymentMethod == null || paymentMethod.isBlank() ? null :
                cb.equal(cb.upper(root.get("paymentMethod")), paymentMethod.trim().toUpperCase());
    }

    public static Specification<PurchaseOrder> hasCreationDateBetween(LocalDateTime from, LocalDateTime to) {
        return (root, query, cb) -> {
            if (from != null && to != null) {
                return cb.between(root.get("creationDate"), from, to);
            } else if (from != null) {
                return cb.greaterThanOrEqualTo(root.get("creationDate"), from);
            } else if (to != null) {
                return cb.lessThanOrEqualTo(root.get("creationDate"), to);
            }
            return null;
        };
    }

    public static Specification<PurchaseOrder> hasPaymentAmountBetween(BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> {
            if (min != null && max != null) {
                return cb.between(root.get("paymentAmount"), min, max);
            } else if (min != null) {
                return cb.greaterThanOrEqualTo(root.get("paymentAmount"), min);
            } else if (max != null) {
                return cb.lessThanOrEqualTo(root.get("paymentAmount"), max);
            }
            return null;
        };
    }
	
	
	
}
