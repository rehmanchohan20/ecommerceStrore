package com.ecomerce.sell.model.Vos;

import com.ecomerce.sell.model.Payment;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PaymentVo {
    private Long paymentId;           // Unique ID of the payment
    private Long orderId;             // Order related to the payment
    private Long orderNumber;       // Human-readable order number (optional)
    private String customerName;      // Customer's name (optional for display)
    private String paymentType;       // CARD, COD, etc.
    private String paymentStatus;     // SUCCESS, FAILED, PENDING
    private String transactionId;     // Stripe or Bank transaction ID
    private BigDecimal amountPaid;        // Final amount paid
    private String currency;          // e.g. PKR, USD
    private Date paymentDate;         // When the payment was made

    public static PaymentVo fromEntity(Payment payment) {
        if (payment == null) return null;

        PaymentVo vo = new PaymentVo();
        vo.setPaymentId(payment.getId());

        if (payment.getOrder() != null) {
            vo.setOrderId(payment.getOrder().getId());
            vo.setOrderNumber(payment.getOrder().getId());
            vo.setCustomerName(payment.getOrder().getUser().getUsername());
            vo.setAmountPaid(payment.getOrder().getTotalAmount());
            vo.setPaymentDate(payment.getOrder().getCreatedAt());
        }

        vo.setPaymentType(payment.getPaymentType());
        vo.setPaymentStatus(payment.getPaymentStatus());
        vo.setTransactionId(payment.getTransactionId());

        return vo;
    }
}
