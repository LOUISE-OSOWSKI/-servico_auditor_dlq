package com.auditor.dlq.adapters.inbound.sqs;

import com.auditor.dlq.domain.model.AuditMessage;
import com.auditor.dlq.domain.model.OrderItem;
import com.auditor.dlq.domain.service.AuditTriageService;
import com.auditor.dlq.ports.outbound.AuditRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Adaptador de entrada (inbound adapter)
 * Escuta a DLQ do SQS e processa mensagens de erro
 * Implementa @SqsListener para consumir mensagens
 */
@Component
public class SqsMessageListener {
    
    private static final Logger logger = LoggerFactory.getLogger(SqsMessageListener.class);
    
    private final ObjectMapper objectMapper;
    private final AuditTriageService auditTriageService;
    private final AuditRepository auditRepository;

    public SqsMessageListener(ObjectMapper objectMapper,
                           AuditTriageService auditTriageService,
                           AuditRepository auditRepository) {
        this.objectMapper = objectMapper;
        this.auditTriageService = auditTriageService;
        this.auditRepository = auditRepository;
    }

    /**
     * Listener que consome mensagens da DLQ do SQS
     * Envolto em try/catch para garantir que a mensagem permaneça na fila se falhar
     * 
     * @param messageJson String JSON bruta da mensagem da SQS
     * @throws RuntimeException se ocorrer erro na persistência, garantindo que
     *                         a mensagem não seja confirmada no SQS
     */
    @SqsListener("${custom.queue.dlq-name}")
    public void listenToErrorQueue(String messageJson) {
        try {
            logger.info("Mensagem recebida da DLQ: {}", messageJson);
            
            // Desserializa o JSON em DTO
            SqsMessageDTO messageDTO = objectMapper.readValue(messageJson, SqsMessageDTO.class);
            
            // Converte DTOs em model domain
            List<OrderItem> orderItems = messageDTO.getOrderItems().stream()
                    .map(dto -> new OrderItem(dto.getSku(), dto.getAmount()))
                    .collect(Collectors.toList());
            
            // Aplica a regra de negócio de triagem
            AuditMessage auditMessage = auditTriageService.triageMessage(
                    messageJson,
                    messageDTO.getZipCode(),
                    messageDTO.getCustomerId(),
                    orderItems,
                    messageDTO.getOrigin(),
                    messageDTO.getOccurredAt()
            );
            
            // Persiste no banco de dados
            auditRepository.save(auditMessage);
            
            logger.info("Mensagem processada e salva com sucesso. ErrorId: {}, Severity: {}",
                    auditMessage.getErrorId(), auditMessage.getSeverity());
            
        } catch (Exception e) {
            logger.error("Erro ao processar mensagem da DLQ: {}", messageJson, e);
            // Lança RuntimeException para que a mensagem NÃO seja confirmada no SQS
            throw new RuntimeException("Falha ao processar mensagem da DLQ", e);
        }
    }
}
