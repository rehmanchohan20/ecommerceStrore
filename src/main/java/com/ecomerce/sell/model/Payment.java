package com.ecomerce.sell.model;

import com.ecomerce.sell.model.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
public class Payment extends BaseEntity {

    @OneToOne
    private Order order;

    private String paymentType; // e.g., CARD, COD
    private String paymentStatus; // e.g., SUCCESS, FAILED
    private String transactionId;
}
