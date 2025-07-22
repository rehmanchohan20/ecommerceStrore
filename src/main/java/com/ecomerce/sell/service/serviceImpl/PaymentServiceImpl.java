package com.ecomerce.sell.service.serviceImpl;

import com.ecomerce.sell.excepotions.DAOResponse;
import com.ecomerce.sell.model.Order;
import com.ecomerce.sell.model.Payment;
import com.ecomerce.sell.model.Vos.PaymentVo;
import com.ecomerce.sell.repository.OrderRepository;
import com.ecomerce.sell.repository.PaymentRepository;
import com.ecomerce.sell.service.PaymentService;
import com.ecomerce.sell.util.AuthUtils;
import com.ecomerce.sell.util.response.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.service.climate.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Value("${stripe.secret.key}")
    private String secretKey;

    @Value("${stripe.success.url}")
    private String successUrl;

    @Value("${stripe.failure.url}")
    private String failureUrl;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    private final Logger logger = Logger.getLogger(PaymentServiceImpl.class.getName());



    @Override
    public Response initiatePayment(PaymentVo paymentVo) {
        Long userId = AuthUtils.getCurrentUserId();  // Authenticated user
        Stripe.apiKey = secretKey;

        Response response = new Response();

        try {
            // Fetch order using orderId and validate user
            Optional<Order> orderOpt = orderRepository.findById(paymentVo.getOrderId());
            if (orderOpt.isEmpty()) {
                response.setResponse(DAOResponse.NO_DATA_FOUND);
                response.setMessage("Order not found");
                return response;
            }

            Order order = orderOpt.get();

            // Optional: Check if the order actually belongs to the logged-in user
            if (!order.getUser().getId().equals(userId)) {
                response.setResponse(DAOResponse.UNAUTHORIZED);
                response.setMessage("Unauthorized access to order");
                return response;
            }

            // Build Stripe session
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl(failureUrl)
                    .putMetadata("order_id", String.valueOf(order.getId()))
                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                    .setPaymentIntentData(
                            SessionCreateParams.PaymentIntentData.builder()
                                    .putMetadata("order_id", String.valueOf(order.getId()))
                                    .putMetadata("user_id", String.valueOf(userId))
                                    .build()
                    )
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("pkr") // or fetch from order.getCurrency()
                                                    .setUnitAmount(order.getTotalAmount().multiply(BigDecimal.valueOf(100)).longValue())
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName("Order #" + order.getId())
                                                                    .build())
                                                    .build())
                                    .build()
                    )
                    .putMetadata("order_id", String.valueOf(order.getId()))
                    .build();

            Session session = Session.create(params);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("url", session.getUrl());

            response.setCode("200");
            response.setMessage("Stripe session created successfully.");
            response.setResponseData(responseData);

        } catch (Exception e) {
            e.printStackTrace();
            response.setResponse(DAOResponse.SYSTEM_ERROR);
            response.setMessage("Stripe session creation failed: " + e.getMessage());
        }

        return response;
    }
    @Override
    public Response handleStripeEvent(Event event) {
        Response response = new Response();

        try {
            String eventType = event.getType();
            logger.info("Stripe event received: " + eventType);

            if ("charge.succeeded".equals(eventType)) {
                return processChargeSucceeded(event);
            } else if ("payment_intent.succeeded".equals(eventType)) {
                return processChargeSucceeded(event);
            } else {
                logger.warning("Unhandled event type: " + eventType);
                response.setResponse(DAOResponse.INVALID_REQUEST);
                response.setMessage("Unhandled event type: " + eventType);
                return response;
            }
        } catch (Exception e) {
            logger.severe("Error handling Stripe webhook: " + e.getMessage());
            e.printStackTrace();
            response.setResponse(DAOResponse.SYSTEM_ERROR);
        }

        return response;
    }


    private Response processChargeSucceeded(Event event) {
        Response response = new Response();
        try {
            // Parse the event payload to JSON
            String rawJson = event.getData().getObject().toJson();
            JsonObject json = JsonParser.parseString(rawJson).getAsJsonObject();

            // Extract core charge details
            String transactionId = json.get("id").getAsString();
            String status = json.get("status").getAsString();

            logger.info("Charge succeeded: Transaction ID = " + transactionId + ", Status = " + status);

            // Extract metadata
            if (!json.has("metadata")) {
                logger.warning("No metadata found in charge");
                response.setResponse(DAOResponse.INVALID_REQUEST);
                response.setMessage("No metadata found in charge");
                return response;
            }

            JsonObject metadata = json.getAsJsonObject("metadata");

            if (!metadata.has("order_id")) {
                logger.warning("Order ID missing in metadata");
                response.setResponse(DAOResponse.INVALID_REQUEST);
                response.setMessage("Order ID not found in metadata");
                return response;
            }

            Long orderId = metadata.get("order_id").getAsLong();
            Long userId = metadata.get("user_id").getAsLong();

            Order order = orderRepository.findByIdAndOrderStatus(orderId, "PENDING");

            if (order != null) {
                logger.info("Order found: " + order.getId());

                order.setOrderStatus("PAID");
                orderRepository.save(order);

                Payment payment = new Payment();
                payment.setOrder(order);
                payment.setCreatedBy(String.valueOf(userId));
                payment.preUpdate();
                payment.setUpdatedBy(String.valueOf(userId));
                payment.setPaymentType("CARD");
                payment.setPaymentStatus("SUCCESS");
                payment.setTransactionId(transactionId);
                paymentRepository.save(payment);

                logger.info("Payment saved successfully for Order ID: " + order.getId());

                response.setResponse(DAOResponse.SUCCESS);
                response.setMessage("Payment processed successfully");
            } else {
                logger.warning("Order not found or not in PENDING status for ID: " + orderId);
                response.setResponse(DAOResponse.NO_DATA_FOUND);
                response.setMessage("Order not found or not in pending state");
            }

        } catch (Exception e) {
            logger.severe("Error in processChargeSucceeded: " + e.getMessage());
            e.printStackTrace();
            response.setResponse(DAOResponse.SYSTEM_ERROR);
            response.setMessage("Exception occurred while processing charge");
        }

        return response;
    }




}
