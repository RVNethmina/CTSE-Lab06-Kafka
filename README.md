# SE4010 Lab 06 — Event-Driven Microservices with Apache Kafka

A Spring Boot microservices demo that demonstrates **event-driven communication** using Apache Kafka. When an order is placed via the API Gateway, an event is published to a Kafka topic and consumed independently by both the Inventory and Billing services.

---

## Architecture

```
Client
  │
  ▼
┌─────────────────────┐
│   API Gateway        │  :8080  (Spring Cloud Gateway)
│  routes /orders/** → │
└────────┬────────────┘
         │ HTTP
         ▼
┌─────────────────────┐
│   Order Service      │  :8081  (Producer)
│  POST /orders        │──── publishes ──▶ Kafka topic: order-topic
└─────────────────────┘
                                        │
              ┌─────────────────────────┴──────────────────────┐
              ▼                                                 ▼
┌──────────────────────────┐                    ┌──────────────────────────┐
│   Inventory Service       │  :8082  (Consumer) │   Billing Service         │  :8083  (Consumer)
│  group: inventory-group   │                    │  group: billing-group     │
│  → Updates stock          │                    │  → Generates invoice      │
└──────────────────────────┘                    └──────────────────────────┘
```

---

## Services

| Service            | Port  | Role                   | Kafka          |
|--------------------|-------|------------------------|----------------|
| **API Gateway**    | 8080  | Routes traffic         | —              |
| **Order Service**  | 8081  | Creates orders         | Producer       |
| **Inventory Service** | 8082 | Processes stock updates | Consumer      |
| **Billing Service** | 8083 | Generates invoices     | Consumer       |
| **Kafka (KRaft)**  | 9092  | Message broker         | —              |

---

## Tech Stack

- **Java 25** · **Spring Boot 4.0.3**
- **Spring Cloud Gateway** (WebFlux) — API Gateway
- **Spring Kafka** — Producer & Consumer integration
- **Apache Kafka 7.6.0** (KRaft mode, no Zookeeper) — via Docker
- **Maven** — build tool

---

## Prerequisites

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (for Kafka)
- Java 25+ & Maven (or use the included `mvnw` wrapper)

---

## Getting Started

### 1. Start Kafka

```bash
docker-compose up -d
```

This starts a single-node Kafka broker in KRaft mode on `localhost:9092`.

### 2. Run the Services

Open a separate terminal for each service and run from its directory:

```bash
# API Gateway
cd ApiGateway/ApiGateway
./mvnw spring-boot:run

# Order Service
cd OrderService/OrderService
./mvnw spring-boot:run

# Inventory Service
cd InventoryService/InventoryService
./mvnw spring-boot:run

# Billing Service
cd BillingService/BillingService
./mvnw spring-boot:run
```

> **Windows users:** use `mvnw.cmd` instead of `./mvnw`.

### 3. Test the Flow

Send a POST request to the API Gateway:

```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: text/plain" \
  -d "Order for 2x Laptop"
```

**Expected response:**
```
Order Created & Event Published
```

**Expected console output in Inventory Service:**
```
Inventory Service received: Order for 2x Laptop
Logic executing: Updating stock...
```

**Expected console output in Billing Service:**
```
Billing Service received: Order for 2x Laptop
Logic executing: Generating invoice...
```

---

## Project Structure

```
Kafka/
├── docker-compose.yml          # Kafka broker (KRaft mode)
├── ApiGateway/
│   └── ApiGateway/
│       └── src/main/
│           ├── java/com/api/ApiGateway/
│           │   └── ApiGatewayApplication.java
│           └── resources/application.yml    # Routes /orders/** → :8081
├── OrderService/
│   └── OrderService/
│       └── src/main/java/com/order/OrderService/
│           ├── OrderController.java         # POST /orders → Kafka producer
│           └── OrderServiceApplication.java
├── BillingService/
│   └── BillingService/
│       └── src/main/java/com/billing/BillingService/
│           ├── BillingEventListener.java    # Kafka consumer (billing-group)
│           └── BillingServiceApplication.java
└── InventoryService/
    └── InventoryService/
        └── src/main/java/com/inventory/InventoryService/
            ├── InventoryEventListener.java  # Kafka consumer (inventory-group)
            └── InventoryServiceApplication.java
```

---

## Kafka Topic

| Topic         | Producer       | Consumers                            |
|---------------|----------------|--------------------------------------|
| `order-topic` | Order Service  | Inventory Service, Billing Service   |

Both consumers use **different consumer group IDs**, so each receives every message independently (fan-out pattern).
