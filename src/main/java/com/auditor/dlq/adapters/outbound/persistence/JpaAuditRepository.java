package com.auditor.dlq.adapters.outbound.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository para a entidade AuditEntity
 * Fornece operações CRUD básicas
 */
@Repository
public interface JpaAuditRepository extends JpaRepository<AuditEntity, String> {
}
