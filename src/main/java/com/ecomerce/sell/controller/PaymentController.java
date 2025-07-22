package com.ecomerce.sell.controller;

import com.ecomerce.sell.excepotions.DAOResponse;
import com.ecomerce.sell.model.Order;
import com.ecomerce.sell.model.Payment;
import com.ecomerce.sell.model.Vos.PaymentVo;
import com.ecomerce.sell.repository.OrderRepository;
import com.ecomerce.sell.repository.PaymentRepository;
import com.ecomerce.sell.service.PaymentService;
import com.ecomerce.sell.util.AuthUtils;
import com.ecomerce.sell.util.response.Response;
import com.fasterxml.jackson.databind.JsonNode;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Charge;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Optional;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @PostMapping("/initiate")
    public ResponseEntity<?> initiatePayment(@RequestBody PaymentVo paymentVo) {
        Response response = null;
        try {
            response = paymentService.initiatePayment(paymentVo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/webhook")// stripe hits back on this url, via this cmd using CLI : stripe listen --forward-to localhost:8080/payment/stripe-webhook
    public ResponseEntity<?> handleStripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
            Response response = paymentService.handleStripeEvent(event);
            return ResponseEntity.ok(response);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Webhook processing failed");
        }
    }

    @GetMapping("/success")
    public String success() {
        return "successfully made an order please check your email for details";
    }

    @GetMapping("/failure")
    public String failure() {
        return "failed to make an order please try again later";
    }
}
