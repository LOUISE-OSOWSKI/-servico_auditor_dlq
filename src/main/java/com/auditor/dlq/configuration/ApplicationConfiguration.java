package com.auditor.dlq.configuration;

import com.auditor.dlq.domain.service.AuditTriageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração de beans da aplicação
 * Define os componentes da camada de domínio e utilitários
 */
@Configuration
public class ApplicationConfiguration {
    
    /**
     * Bean para serviço de triagem de auditoria
     */
    @Bean
    public AuditTriageService auditTriageService() {
        return new AuditTriageService();
    }
    
    /**
     * Bean para ObjectMapper do Jackson
     * Utilizado para desserializar JSON das mensagens SQS
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
