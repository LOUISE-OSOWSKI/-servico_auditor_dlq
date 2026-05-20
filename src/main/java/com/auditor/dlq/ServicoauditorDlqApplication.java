package com.auditor.dlq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principal da aplicação
 * Microsserviço de auditoria de DLQ do SQS
 */
@SpringBootApplication
public class ServicoauditorDlqApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServicoauditorDlqApplication.class, args);
    }
}
