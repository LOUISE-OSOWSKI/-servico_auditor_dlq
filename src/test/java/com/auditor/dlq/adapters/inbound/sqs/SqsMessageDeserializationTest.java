package com.auditor.dlq.adapters.inbound.sqs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para deserialização de mensagens SQS
 */
@SpringBootTest
class SqsMessageDeserializationTest {
    
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldDeserializeSqsMessageCorrectly() throws Exception {
        // Arrange
        String jsonMessage = """
            {
                "zipCode": "80010000",
                "customerId": 1,
                "orderItems": [
                    { "sku": 1, "amount": 5 },
                    { "sku": 2, "amount": 3 }
                ],
                "origin": "SQS_QUEUE",
                "occurredAt": "2024-05-20T14:30:00Z"
            }
            """;
        
        // Act
        SqsMessageDTO result = objectMapper.readValue(jsonMessage, SqsMessageDTO.class);
        
        // Assert
        assertNotNull(result);
        assertEquals("80010000", result.getZipCode());
        assertEquals(1, result.getCustomerId());
        assertEquals(2, result.getOrderItems().size());
        assertEquals(5, result.getOrderItems().get(0).getAmount());
        assertEquals("SQS_QUEUE", result.getOrigin());
        assertNotNull(result.getOccurredAt());
    }

    @Test
    void shouldDeserializeOrderItemsCorrectly() throws Exception {
        // Arrange
        String jsonMessage = """
            {
                "zipCode": "12345000",
                "customerId": 99,
                "orderItems": [
                    { "sku": 101, "amount": 50 },
                    { "sku": 102, "amount": 30 },
                    { "sku": 103, "amount": 20 }
                ],
                "origin": "SQS_QUEUE",
                "occurredAt": "2024-05-20T14:30:00Z"
            }
            """;
        
        // Act
        SqsMessageDTO result = objectMapper.readValue(jsonMessage, SqsMessageDTO.class);
        
        // Assert
        assertEquals(3, result.getOrderItems().size());
        assertEquals(101, result.getOrderItems().get(0).getSku());
        assertEquals(102, result.getOrderItems().get(1).getSku());
        assertEquals(103, result.getOrderItems().get(2).getSku());
    }
}
