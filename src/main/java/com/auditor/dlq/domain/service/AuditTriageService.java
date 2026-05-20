package com.auditor.dlq.domain.service;

import com.auditor.dlq.domain.model.AuditMessage;
import com.auditor.dlq.domain.model.OrderItem;
import com.auditor.dlq.domain.model.Severity;

import java.time.Instant;
import java.util.List;

/**
 * Serviço de Domínio que implementa a regra de negócio de triagem
 * Responsável por calcular a severidade baseado na quantidade de produtos
 */
public class AuditTriageService {

    /**
     * Realiza a triagem de severidade e cria uma mensagem de auditoria
     * 
     * @param payload String JSON bruta da mensagem
     * @param zipCode CEP do cliente
     * @param customerId ID do cliente
     * @param orderItems Lista de itens do pedido
     * @param origin Origem da mensagem (SQS_QUEUE)
     * @param occurredAt Quando o erro ocorreu
     * @return AuditMessage com severidade calculada
     */
    public AuditMessage triageMessage(
            String payload,
            String zipCode,
            int customerId,
            List<OrderItem> orderItems,
            String origin,
            Instant occurredAt) {

        // Calcula o total acumulado dos amounts
        int totalAmount = orderItems.stream()
                .mapToInt(OrderItem::getAmount)
                .sum();

        // Determina a severidade baseado na regra de negócio
        Severity severity = AuditMessage.calculateSeverity(totalAmount);

        // Constrói a entidade de domínio
        return new AuditMessage.Builder()
                .withPayload(payload)
                .withZipCode(zipCode)
                .withCustomerId(customerId)
                .withOrderItems(orderItems)
                .withOrigin(origin)
                .withOccurredAt(occurredAt)
                .withSeverity(severity)
                .withTimestamp(Instant.now())
                .build();
    }
}
