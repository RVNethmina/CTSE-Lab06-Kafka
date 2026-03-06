package com.billing.BillingService;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class BillingEventListener {

    @KafkaListener(topics = "order-topic", groupId = "billing-group")
    public void handleOrderCreatedEvent(String orderData) {
        System.out.println("Billing Service received: " + orderData);
        System.out.println("Logic executing: Generating invoice...");
    }
}