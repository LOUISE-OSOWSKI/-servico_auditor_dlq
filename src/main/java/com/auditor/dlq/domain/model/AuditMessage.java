package com.auditor.dlq.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Entidade de Domínio que representa uma mensagem de auditoria
 * Contém a lógica de negócio para triagem de severidade
 * Classe pura sem anotações de framework
 */
public class AuditMessage {
    private final String errorId;
    private final String queueName;
    private final String payload;
    private final Instant timestamp;
    private final String status;
    private final Severity severity;
    
    // Dados do payload desserializados
    private final String zipCode;
    private final int customerId;
    private final List<OrderItem> orderItems;
    private final String origin;
    private final Instant occurredAt;

    private AuditMessage(Builder builder) {
        this.errorId = builder.errorId;
        this.queueName = builder.queueName;
        this.payload = builder.payload;
        this.timestamp = builder.timestamp;
        this.status = builder.status;
        this.severity = builder.severity;
        this.zipCode = builder.zipCode;
        this.customerId = builder.customerId;
        this.orderItems = builder.orderItems;
        this.origin = builder.origin;
        this.occurredAt = builder.occurredAt;
    }

    /**
     * Calcula a severidade baseado na soma dos amounts
     * - Total > 100 -> HIGH
     * - Total entre 50 e 100 (inclusive) -> MEDIUM
     * - Total < 50 -> LOW
     */
    public static Severity calculateSeverity(int totalAmount) {
        if (totalAmount > 100) {
            return Severity.HIGH;
        } else if (totalAmount >= 50) {
            return Severity.MEDIUM;
        } else {
            return Severity.LOW;
        }
    }

    // Getters
    public String getErrorId() {
        return errorId;
    }

    public String getQueueName() {
        return queueName;
    }

    public String getPayload() {
        return payload;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getStatus() {
        return status;
    }

    public Severity getSeverity() {
        return severity;
    }

    public String getZipCode() {
        return zipCode;
    }

    public int getCustomerId() {
        return customerId;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public String getOrigin() {
        return origin;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    // Builder Pattern
    public static class Builder {
        private String errorId = UUID.randomUUID().toString();
        private String queueName = "T0XN_servico_auditor_dlq";
        private String payload;
        private Instant timestamp = Instant.now();
        private String status = "PENDING_ANALYSIS";
        private Severity severity;
        private String zipCode;
        private int customerId;
        private List<OrderItem> orderItems;
        private String origin;
        private Instant occurredAt;

        public Builder withPayload(String payload) {
            this.payload = payload;
            return this;
        }

        public Builder withSeverity(Severity severity) {
            this.severity = severity;
            return this;
        }

        public Builder withZipCode(String zipCode) {
            this.zipCode = zipCode;
            return this;
        }

        public Builder withCustomerId(int customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder withOrderItems(List<OrderItem> orderItems) {
            this.orderItems = orderItems;
            return this;
        }

        public Builder withOrigin(String origin) {
            this.origin = origin;
            return this;
        }

        public Builder withOccurredAt(Instant occurredAt) {
            this.occurredAt = occurredAt;
            return this;
        }

        public Builder withTimestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public AuditMessage build() {
            return new AuditMessage(this);
        }
    }
}
