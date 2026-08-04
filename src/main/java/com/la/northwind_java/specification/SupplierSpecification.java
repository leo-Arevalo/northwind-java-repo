
package com.la.northwind_java.specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;

import com.la.northwind_java.models.PurchaseOrder;
import com.la.northwind_java.models.Supplier;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

public class SupplierSpecification {

	public static Specification<Supplier> hasCompanyLike(String company){
		return (root, query, cb) -> 
		(company == null || company.trim().isEmpty())
		? null 
		: cb.like(cb.upper(root.get("company")), "%" + company.trim().toUpperCase() +"%");
	}
	
	public static Specification<Supplier> hasEmail(String email){
		return (root, query, cb) -> email == null ? null :
			cb.like(cb.lower(root.get("emailAddress")),"%" + email.trim().toLowerCase() + "%");
	}
	
	public static Specification<Supplier> livesInCity(String city){
		return (root, query, cb) -> city == null ? null :
			cb.like(cb.lower(root.get("city")),"%" +city.trim().toLowerCase() + "%");
	}
	
	public static Specification<Supplier> liveInCountry(String country){
		return (root, query, cb) -> country == null ? null :
			cb.like(cb.lower(root.get("country")), "%" + country.trim().toLowerCase() + "%");
	}
	
	public static Specification<Supplier> hasJobTitle(String jobTitle){
		return (root, query, cb) -> jobTitle == null ? null :
			cb.equal(cb.lower(root.get("jobTitle")), jobTitle.trim().toLowerCase());
	}
	
	public static Specification<Supplier> hasName(String name) {
		return (root, query, cb) -> {
			if(name == null) return null;
			String pattern = "%" + name.trim().toLowerCase() +"%";
			return cb.or(
					cb.like(cb.lower(root.get("firstName")), pattern),
					cb.like(cb.lower(root.get("lastName")), pattern)
					);
		};
	}
	
	// Filtros relacionados a Purchase Order (ordenes de compra)
	
	
	/*
	 * Verifica si un proveedor tiene órdenes o no
	 */
	
	public static Specification<Supplier> hasOrders(Boolean hasOrd){
		return (root, query, cb) -> {
			if(hasOrd == null) return null; //si el parametro es nulo no aplica filtros
			
			//realizamos un LEFT JOIN con la relación "purchaseOrders" del proveedor donde root = supplier
			Join<Object,Object> orders = root.join("purchaseOrder", JoinType.LEFT);
			query.distinct(true); //evitamos duplicados por el JOIN
			
			//si se busca proveedores con órdenes, verificamos que el campo 'id' de la orden no sea nulo
			if(hasOrd) {
				return cb.isNotNull(orders.get("id")); //where purchaseOrders.id IS NOT NULL
			} else {
				//si se busca proveedores sin ordenes, verifica que 'id' de la orden sea nulo
				return cb.isNull(orders.get("id")); //WHERE purchaseOrders.id IS NULL
			}
		};
	}
	
	//Especificación para filtrar proveedores según la fecha de sus órdenes
	public static Specification<Supplier> hasOrderDateBetween(LocalDateTime from, LocalDateTime to){
		
		return (root, query, cb) -> {
			//si no tiene parametros no se aplican filtros
			if(from == null && to == null) return null; 
			
			//Realizamos un LEFT JOIN con la relación "purchaseOrders"
			Join<Object, Object> orders = root.join("purchaseOrder", JoinType.LEFT);
			//nos aseguramos que los resultados son unicos
			query.distinct(true);
			
			//si se proporcionan ambas fechas, aplica un filtro BETWEEN
			
			if(from != null && to != null) {
				return cb.between(orders.get("ordersDate"), from, to);
			}else if(from != null) {
				//solo fecha desde: mayores o iguales a 'from'
				return cb.greaterThanOrEqualTo(orders.get("orderDate"), from);
			}else {
				//solo fecha hasta: menores o iguales a 'to'
				return cb.lessThanOrEqualTo(orders.get("orderDate"), to);
			}
		};	
	}
	
	//Especificación para filtrar proveedores según el total de pago de sus órdenes
	
	public static Specification<Supplier> hasTotalPaymentBetween(BigDecimal min, BigDecimal max){
		return (root, query, cb) -> {
			//si no se proporciona ningún límite, no se aplican filtros
			if(min == null && max == null) return null;
			
			//JOIN con la relación "purchaseOrders"
			Join<Object, Object> orders = root.join("purchaseOrder", JoinType.LEFT);
			
			//Evita resultados duplicados
			query.distinct(true);
			
			//Aplica los filtros de pago mínimo y máximo
			if(min !=null && max != null) {
				return cb.between(orders.get("totalPayment"), min, max); //BETWEEN min AND max
			}else if(min != null) {
				return cb.greaterThanOrEqualTo(orders.get("totalPayment"), min); 
			}else {
				return cb.lessThanOrEqualTo(orders.get("totalPayment"), max);
			}
			
		};
	}
	
	
	
	//filtro por fecha de creacion
	public static Specification<Supplier> hasOrderCreatedAfter(LocalDateTime date){
		return (root, query, cb) -> {
			if(date == null) return null;
			Join<Supplier, PurchaseOrder> orders = root.join("purchaseOrders", JoinType.LEFT);
			return cb.greaterThanOrEqualTo(orders.get("creationDate"), date);
		};
	}
	//filtro sobre purchaseOrder por el estado
	public static Specification<Supplier> hasOrderWithStatus(Integer statusId){
		return(root, query, cb)->{
			if(statusId == null) return null;
			Join<Supplier, PurchaseOrder> orders = root.join("purchaseOrders", JoinType.LEFT);
			return cb.equal(orders.get("status").get("id"),statusId);
		};
	}
	
	//Por monto minimo de paymentAmount 
	
	public static Specification<Supplier> hasOrderWithMinPayment(BigDecimal amount){
		return(root, query, cb) -> {
			Join<Supplier, PurchaseOrder> orders = root.join("purchaseOrders",JoinType.LEFT);
			return cb.greaterThanOrEqualTo(orders.get("paymentAmount"), amount);
		};
	}
	
	
	
}









