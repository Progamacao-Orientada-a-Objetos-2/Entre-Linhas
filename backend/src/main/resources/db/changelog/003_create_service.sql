--liquibase formatted sql

--changeset josewynder:003
CREATE TABLE service (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    parent_service_id BIGINT,
    CONSTRAINT fk_parent_service
        FOREIGN KEY (parent_service_id) REFERENCES service(id)
        ON DELETE SET NULL
);
