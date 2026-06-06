-- BugVault schema
-- Deliberately designed with IDOR-vulnerable structure (no row-level security)

CREATE TABLE users (
    id         BIGSERIAL PRIMARY KEY,
    username   VARCHAR(100)  NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role       VARCHAR(50)   NOT NULL DEFAULT 'USER',
    enabled    BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

-- notes.owner_id exists but is NOT enforced at query level (V4 IDOR)
CREATE TABLE notes (
    id         BIGSERIAL PRIMARY KEY,
    owner_id   BIGINT        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title      VARCHAR(255)  NOT NULL,
    body       TEXT,
    visibility VARCHAR(20)   NOT NULL DEFAULT 'PRIVATE',
    created_at TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

CREATE TABLE attachments (
    id                BIGSERIAL PRIMARY KEY,
    note_id           BIGINT       NOT NULL REFERENCES notes(id) ON DELETE CASCADE,
    stored_path       VARCHAR(500) NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    content_type      VARCHAR(100),
    uploaded_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- Same schema as appsec-java-core: actor/action/target/meta
CREATE TABLE audit_events (
    id         BIGSERIAL PRIMARY KEY,
    event_time TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    actor      VARCHAR(255) NOT NULL,
    action     VARCHAR(100) NOT NULL,
    target     VARCHAR(255) NOT NULL,
    meta       TEXT         DEFAULT '{}'
);

CREATE INDEX idx_audit_event_time ON audit_events(event_time DESC);
CREATE INDEX idx_audit_actor      ON audit_events(actor);
CREATE INDEX idx_notes_owner      ON notes(owner_id);
