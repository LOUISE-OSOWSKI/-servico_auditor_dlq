package com.auditor.dlq.ports.inbound;

/**
 * Porto de entrada (inbound port)
 * Define o contrato de caso de uso para processar mensagens de DLQ
 * Interface que será implementada pelo adaptador SQS
 */
public interface AuditProcessingUseCase {
    
    /**
     * Processa uma mensagem de erro da DLQ
     * 
     * @param messageJson String JSON bruta da mensagem
     * @throws RuntimeException se o processamento falhar, garantindo que a mensagem
     *                         permaneça na DLQ
     */
    void processErrorMessage(String messageJson);
}
