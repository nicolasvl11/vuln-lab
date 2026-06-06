package com.bugvault.audit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class AuditPublisher {

    private final AuditEventRepository repository;

    public void publish(String actor, String action, String target, String meta) {
        repository.save(AuditEvent.builder()
                .eventTime(Instant.now())
                .actor(actor)
                .action(action)
                .target(target)
                .meta(meta)
                .build());
    }

    public void publish(String actor, String action, String target) {
        publish(actor, action, target, "{}");
    }
}
