package com.inventory.InventoryService;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class InventoryEventListener {

    @KafkaListener(topics = "order-topic", groupId = "inventory-group")
    public void handleOrderCreatedEvent(String orderData) {
        System.out.println("Inventory Service received: " + orderData);
        System.out.println("Logic executing: Updating stock...");
    }
}