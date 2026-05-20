package com.auditor.dlq.domain.service;

import com.auditor.dlq.domain.model.AuditMessage;
import com.auditor.dlq.domain.model.OrderItem;
import com.auditor.dlq.domain.model.Severity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para o serviço de triagem de auditoria
 */
class AuditTriageServiceTest {
    
    private AuditTriageService auditTriageService;

    @BeforeEach
    void setUp() {
        auditTriageService = new AuditTriageService();
    }

    @Test
    void shouldTriageAsLowSeverity_WhenTotalAmountLessThan50() {
        // Arrange
        List<OrderItem> orderItems = Arrays.asList(
                new OrderItem(1, 20),
                new OrderItem(2, 15),
                new OrderItem(3, 10)
        );
        // Total: 45
        
        // Act
        AuditMessage result = auditTriageService.triageMessage(
                "{...}",
                "80010000",
                1,
                orderItems,
                "SQS_QUEUE",
                Instant.now()
        );
        
        // Assert
        assertEquals(Severity.LOW, result.getSeverity());
    }

    @Test
    void shouldTriageAsMediumSeverity_WhenTotalAmountBetween50And100() {
        // Arrange
        List<OrderItem> orderItems = Arrays.asList(
                new OrderItem(1, 35),
                new OrderItem(2, 30),
                new OrderItem(3, 20)
        );
        // Total: 85
        
        // Act
        AuditMessage result = auditTriageService.triageMessage(
                "{...}",
                "80010000",
                1,
                orderItems,
                "SQS_QUEUE",
                Instant.now()
        );
        
        // Assert
        assertEquals(Severity.MEDIUM, result.getSeverity());
    }

    @Test
    void shouldTriageAsHighSeverity_WhenTotalAmountGreaterThan100() {
        // Arrange
        List<OrderItem> orderItems = Arrays.asList(
                new OrderItem(1, 60),
                new OrderItem(2, 55)
        );
        // Total: 115
        
        // Act
        AuditMessage result = auditTriageService.triageMessage(
                "{...}",
                "80010000",
                1,
                orderItems,
                "SQS_QUEUE",
                Instant.now()
        );
        
        // Assert
        assertEquals(Severity.HIGH, result.getSeverity());
    }

    @Test
    void shouldTriageAsLowSeverity_WhenTotalAmountEquals49() {
        // Arrange
        List<OrderItem> orderItems = Arrays.asList(
                new OrderItem(1, 25),
                new OrderItem(2, 24)
        );
        // Total: 49
        
        // Act
        AuditMessage result = auditTriageService.triageMessage(
                "{...}",
                "80010000",
                1,
                orderItems,
                "SQS_QUEUE",
                Instant.now()
        );
        
        // Assert
        assertEquals(Severity.LOW, result.getSeverity());
    }

    @Test
    void shouldTriageAsMediumSeverity_WhenTotalAmountEquals50() {
        // Arrange
        List<OrderItem> orderItems = Arrays.asList(
                new OrderItem(1, 25),
                new OrderItem(2, 25)
        );
        // Total: 50
        
        // Act
        AuditMessage result = auditTriageService.triageMessage(
                "{...}",
                "80010000",
                1,
                orderItems,
                "SQS_QUEUE",
                Instant.now()
        );
        
        // Assert
        assertEquals(Severity.MEDIUM, result.getSeverity());
    }

    @Test
    void shouldTriageAsMediumSeverity_WhenTotalAmountEquals100() {
        // Arrange
        List<OrderItem> orderItems = Arrays.asList(
                new OrderItem(1, 50),
                new OrderItem(2, 50)
        );
        // Total: 100
        
        // Act
        AuditMessage result = auditTriageService.triageMessage(
                "{...}",
                "80010000",
                1,
                orderItems,
                "SQS_QUEUE",
                Instant.now()
        );
        
        // Assert
        assertEquals(Severity.MEDIUM, result.getSeverity());
    }

    @Test
    void shouldTriageAsHighSeverity_WhenTotalAmountEquals101() {
        // Arrange
        List<OrderItem> orderItems = Arrays.asList(
                new OrderItem(1, 50),
                new OrderItem(2, 51)
        );
        // Total: 101
        
        // Act
        AuditMessage result = auditTriageService.triageMessage(
                "{...}",
                "80010000",
                1,
                orderItems,
                "SQS_QUEUE",
                Instant.now()
        );
        
        // Assert
        assertEquals(Severity.HIGH, result.getSeverity());
    }

    @Test
    void shouldIncludeAllMessageData_InResult() {
        // Arrange
        List<OrderItem> orderItems = Arrays.asList(
                new OrderItem(1, 30),
                new OrderItem(2, 20)
        );
        Instant now = Instant.now();
        
        // Act
        AuditMessage result = auditTriageService.triageMessage(
                "{test}",
                "12345000",
                123,
                orderItems,
                "SQS_QUEUE",
                now
        );
        
        // Assert
        assertEquals("{test}", result.getPayload());
        assertEquals("12345000", result.getZipCode());
        assertEquals(123, result.getCustomerId());
        assertEquals(2, result.getOrderItems().size());
        assertEquals("SQS_QUEUE", result.getOrigin());
        assertEquals(now, result.getOccurredAt());
        assertEquals("T0XN_servico_auditor_dlq", result.getQueueName());
        assertEquals("PENDING_ANALYSIS", result.getStatus());
    }
}
