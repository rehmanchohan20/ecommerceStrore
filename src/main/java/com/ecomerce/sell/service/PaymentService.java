package com.ecomerce.sell.service;

import com.ecomerce.sell.model.Vos.PaymentVo;
import com.ecomerce.sell.util.response.Response;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;

public interface PaymentService {
    Response initiatePayment(PaymentVo paymentVo);
    Response handleStripeEvent(Event event);
}
