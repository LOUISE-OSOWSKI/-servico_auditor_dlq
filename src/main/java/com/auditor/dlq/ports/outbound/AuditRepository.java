package com.auditor.dlq.ports.outbound;

import com.auditor.dlq.domain.model.AuditMessage;

/**
 * Porto de saída (outbound port)
 * Define o contrato para persistência de mensagens de auditoria
 * Implementado pelo adaptador de persistência JPA
 */
public interface AuditRepository {
    
    /**
     * Salva uma mensagem de auditoria no banco de dados
     * 
     * @param auditMessage Mensagem de auditoria a ser persistida
     * @return A mensagem persistida com ID atribuído
     */
    AuditMessage save(AuditMessage auditMessage);
    
    /**
     * Busca uma mensagem de auditoria pelo ID
     * 
     * @param errorId ID único da mensagem de auditoria
     * @return A mensagem de auditoria encontrada
     */
    AuditMessage findById(String errorId);
}
