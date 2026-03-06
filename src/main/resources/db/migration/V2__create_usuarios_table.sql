CREATE TABLE usuarios (
    id         BIGSERIAL       PRIMARY KEY,
    nombre     VARCHAR(150)    NOT NULL,
    email      VARCHAR(200)    NOT NULL UNIQUE,
    contrasena VARCHAR(300)    NOT NULL
);