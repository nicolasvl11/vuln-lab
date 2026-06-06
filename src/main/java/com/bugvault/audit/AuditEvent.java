package com.bugvault.audit;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "audit_events",
        indexes = {
                @Index(name = "idx_audit_event_time", columnList = "event_time DESC"),
                @Index(name = "idx_audit_actor", columnList = "actor")
        })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_time", nullable = false)
    @Builder.Default
    private Instant eventTime = Instant.now();

    @Column(nullable = false)
    private String actor;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String target;

    @Column(name = "meta")
    @Builder.Default
    private String meta = "{}";
}
