package com.auditor.dlq.adapters.outbound.persistence;

import com.auditor.dlq.domain.model.AuditMessage;
import com.auditor.dlq.domain.model.OrderItem;
import com.auditor.dlq.ports.outbound.AuditRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adaptador de saída (outbound adapter)
 * Implementa a interface AuditRepository usando Spring Data JPA
 * Converte entre a entidade JPA e o modelo de domínio
 */
@Component
public class PersistenceAuditRepositoryAdapter implements AuditRepository {
    
    private final JpaAuditRepository jpaAuditRepository;

    public PersistenceAuditRepositoryAdapter(JpaAuditRepository jpaAuditRepository) {
        this.jpaAuditRepository = jpaAuditRepository;
    }

    @Override
    public AuditMessage save(AuditMessage auditMessage) {
        // Converte o modelo de domínio para entidade JPA
        AuditEntity entity = new AuditEntity(
                auditMessage.getErrorId(),
                auditMessage.getQueueName(),
                auditMessage.getPayload(),
                auditMessage.getTimestamp(),
                auditMessage.getStatus(),
                auditMessage.getSeverity().getValue()
        );
        
        // Persiste no banco de dados
        AuditEntity savedEntity = jpaAuditRepository.save(entity);
        
        // Converte de volta para o modelo de domínio
        return convertToDomain(savedEntity);
    }

    @Override
    public AuditMessage findById(String errorId) {
        Optional<AuditEntity> entity = jpaAuditRepository.findById(errorId);
        return entity.map(this::convertToDomain).orElse(null);
    }

    /**
     * Converte uma AuditEntity JPA para o modelo de domínio AuditMessage
     */
    private AuditMessage convertToDomain(AuditEntity entity) {
        return new AuditMessage.Builder()
                .withPayload(entity.getPayload())
                // Nota: O modelo de domínio contém mais informações que não são persistidas na entidade JPA
                // Para conversão completa, seria necessário deserializar o payload
                .build();
    }
}
