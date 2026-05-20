package com.auditor.dlq.domain.model;

/**
 * Value Object que representa um item de pedido
 * Classe pura do domínio sem anotações de framework
 */
public class OrderItem {
    private final int sku;
    private final int amount;

    public OrderItem(int sku, int amount) {
        this.sku = sku;
        this.amount = amount;
    }

    public int getSku() {
        return sku;
    }

    public int getAmount() {
        return amount;
    }
}
