CREATE TABLE topicos (
    id            BIGSERIAL       PRIMARY KEY,
    titulo        VARCHAR(300)    NOT NULL UNIQUE,
    mensaje       TEXT            NOT NULL,
    fecha_creacion TIMESTAMP      NOT NULL DEFAULT NOW(),
    estado        VARCHAR(20)     NOT NULL DEFAULT 'ABIERTO',
    autor         VARCHAR(150)    NOT NULL,
    curso         VARCHAR(100)    NOT NULL,

    CONSTRAINT chk_estado CHECK (estado IN ('ABIERTO', 'CERRADO', 'ELIMINADO'))
);