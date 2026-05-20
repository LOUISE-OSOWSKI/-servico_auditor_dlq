package com.auditor.dlq.adapters.inbound.sqs;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.List;

/**
 * DTO para deserialização de mensagens SQS
 * Mapeia o JSON bruto da fila para uma estrutura Java
 */
public class SqsMessageDTO {
    
    @JsonProperty("zipCode")
    private String zipCode;
    
    @JsonProperty("customerId")
    private int customerId;
    
    @JsonProperty("orderItems")
    private List<OrderItemDTO> orderItems;
    
    @JsonProperty("origin")
    private String origin;
    
    @JsonProperty("occurredAt")
    private Instant occurredAt;

    // Constructors
    public SqsMessageDTO() {}

    public SqsMessageDTO(String zipCode, int customerId, List<OrderItemDTO> orderItems,
                        String origin, Instant occurredAt) {
        this.zipCode = zipCode;
        this.customerId = customerId;
        this.orderItems = orderItems;
        this.origin = origin;
        this.occurredAt = occurredAt;
    }

    // Getters and Setters
    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public List<OrderItemDTO> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemDTO> orderItems) {
        this.orderItems = orderItems;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }
}
