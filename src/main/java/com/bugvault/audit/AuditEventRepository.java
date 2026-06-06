package com.bugvault.audit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface AuditEventRepository extends JpaRepository<AuditEvent, Long> {

    List<AuditEvent> findByActorOrderByEventTimeDesc(String actor);

    List<AuditEvent> findByActionOrderByEventTimeDesc(String action);

    List<AuditEvent> findByEventTimeAfterOrderByEventTimeDesc(Instant since);
}
