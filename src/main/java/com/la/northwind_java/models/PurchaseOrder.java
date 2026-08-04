package com.la.northwind_java.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.la.northwind_java.models.*;

/**
 * Represents a purchase order in the system.
 * 
 * @author LeO
 */
@Entity
@Table(name = "purchase_orders", indexes = {
        @Index(name = "idx_supplier_id", columnList = "supplier_id"),
        @Index(name = "idx_status_id", columnList = "status_id"),
        @Index(name = "idx_created_by", columnList = "created_by")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrder {

    /**
     * Unique identifier for the purchase order.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Supplier related to the purchase order.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    @NotNull
    private Supplier supplier;

    /**
     * Employee who created the purchase order.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    @NotNull
    private Employee createdBy;

    /**
     * Date when the order was submitted.
     */
    @PastOrPresent
    @Column(name = "submitted_date")
    private LocalDateTime submittedDate;

    /**
     * Date when the order was created.
     */
    @PastOrPresent
    @Column(name = "creation_date", nullable = false)
    @NotNull
    private LocalDateTime creationDate;

    /**
     * Status of the purchase order.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    @NotNull
    private PurchaseOrderStatus status;

    /**
     * Expected date for order fulfillment.
     */
    @Column(name = "expected_date")
    private LocalDateTime expectedDate;

    /**
     * Shipping fee.
     */
    @DecimalMin("0.00")
    @Column(name = "shipping_fee", nullable = false, precision = 19, scale = 4)
    private BigDecimal shippingFee;

    /**
     * Taxes applied to the order.
     */
    @DecimalMin("0.00")
    @Column(name = "taxes", nullable = false, precision = 19, scale = 4)
    private BigDecimal taxes;

    /**
     * Payment details.
     */
    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @DecimalMin("0.00")
    @Column(name = "payment_amount", precision = 19, scale = 4)
    private BigDecimal paymentAmount;

    @Size(max = 50)
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Lob
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private Employee approvedBy;

    @Column(name = "approved_date")
    private LocalDateTime approvedDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by")
    private Employee submittedBy;

    /**
     * Normalize string data before persisting or updating.
     */
    @PrePersist
    @PreUpdate
    private void normalizeData() {
        if (paymentMethod != null) {
            paymentMethod = paymentMethod.trim().toUpperCase();
        }
    }
}
