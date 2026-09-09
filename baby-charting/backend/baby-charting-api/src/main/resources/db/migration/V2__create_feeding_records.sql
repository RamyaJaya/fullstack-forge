CREATE TABLE feeding_records (
    id              BIGSERIAL       PRIMARY KEY,
    baby_id         BIGINT          NOT NULL REFERENCES babies(id),
    type            VARCHAR(16)     NOT NULL,
    occurred_at     TIMESTAMPTZ     NOT NULL,
    amount_ml       INTEGER,
    notes           VARCHAR(500),
    created_at      TIMESTAMPTZ     NOT NULL,
    updated_at      TIMESTAMPTZ     NOT NULL,

    CONSTRAINT chk_feeding_records_type
        CHECK (type IN ('BREAST_MILK', 'FORMULA')),

    CONSTRAINT chk_feeding_records_amount_ml
        CHECK (amount_ml IS NULL OR amount_ml > 0)
);

CREATE INDEX idx_feeding_records_baby_id_occurred_at
    ON feeding_records (baby_id, occurred_at DESC);
