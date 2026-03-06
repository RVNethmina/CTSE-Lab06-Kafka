package com.order.OrderService;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public OrderController (KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping
    public String createOrder (@RequestBody String order) {
        kafkaTemplate.send("order-topic", order);
        return "Order Created & Event Published";
    }
}