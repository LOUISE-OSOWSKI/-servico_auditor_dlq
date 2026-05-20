package com.auditor.dlq.adapters.inbound.sqs;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para deserialização de itens do pedido
 * Utilizado no adaptador SQS para mapear JSON para objetos Java
 */
public class OrderItemDTO {
    
    @JsonProperty("sku")
    private int sku;
    
    @JsonProperty("amount")
    private int amount;

    // Constructors
    public OrderItemDTO() {}

    public OrderItemDTO(int sku, int amount) {
        this.sku = sku;
        this.amount = amount;
    }

    // Getters and Setters
    public int getSku() {
        return sku;
    }

    public void setSku(int sku) {
        this.sku = sku;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
