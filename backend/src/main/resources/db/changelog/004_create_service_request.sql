--liquibase formatted sql

--changeset josewynder:004
CREATE TABLE service_request (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    company_id BIGINT NOT NULL,
    faction_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT fk_service_request_company
        FOREIGN KEY (company_id) REFERENCES user_account(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_service_request_faction
        FOREIGN KEY (faction_id) REFERENCES user_account(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_service_request_service
        FOREIGN KEY (service_id) REFERENCES service(id)
        ON DELETE RESTRICT
);
