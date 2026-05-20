package com.auditor.dlq.domain.model;

/**
 * Enum que representa os níveis de severidade
 * Classificação baseada na quantidade total de produtos
 */
public enum Severity {
    HIGH("HIGH"),
    MEDIUM("MEDIUM"),
    LOW("LOW");

    private final String value;

    Severity(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
