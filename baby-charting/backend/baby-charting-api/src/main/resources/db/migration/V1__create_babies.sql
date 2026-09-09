CREATE TABLE babies (
    id              BIGSERIAL       PRIMARY KEY,
    first_name      VARCHAR(100)    NOT NULL,
    last_name       VARCHAR(100)    NOT NULL,
    date_of_birth   DATE            NOT NULL,
    sex             VARCHAR(16),
    created_at      TIMESTAMPTZ     NOT NULL,
    updated_at      TIMESTAMPTZ     NOT NULL,

    CONSTRAINT chk_babies_sex
        CHECK (sex IS NULL OR sex IN ('MALE', 'FEMALE', 'UNKNOWN'))
);
