-- M4 fix: allow tote reuse while enforcing a single OPEN packing session per tote

ALTER TABLE packing_sessions
    DROP CONSTRAINT IF EXISTS uk_packing_sessions_tote;

CREATE UNIQUE INDEX IF NOT EXISTS uk_packing_sessions_tote_open
    ON packing_sessions(tote_id)
    WHERE status = 'OPEN';

