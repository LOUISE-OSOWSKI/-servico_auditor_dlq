package com.auditor.dlq.adapters.outbound.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * Entidade JPA para persistência
 * Mapeia o modelo de domínio para a tabela do banco de dados H2
 */
@Entity
@Table(name = "audit_messages")
public class AuditEntity {
    
    @Id
    @Column(name = "error_id", nullable = false)
    private String errorId;
    
    @Column(name = "queue_name", nullable = false)
    private String queueName;
    
    @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
    private String payload;
    
    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;
    
    @Column(name = "status", nullable = false)
    private String status;
    
    @Column(name = "severity", nullable = false)
    private String severity;

    // Constructors
    public AuditEntity() {}

    public AuditEntity(String errorId, String queueName, String payload,
                      Instant timestamp, String status, String severity) {
        this.errorId = errorId;
        this.queueName = queueName;
        this.payload = payload;
        this.timestamp = timestamp;
        this.status = status;
        this.severity = severity;
    }

    // Getters and Setters
    public String getErrorId() {
        return errorId;
    }

    public void setErrorId(String errorId) {
        this.errorId = errorId;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }
}
