--liquibase formatted sql
--changeset entrelinhas:014
ALTER TABLE review ADD COLUMN IF NOT EXISTS service_request_id BIGINT NOT NULL;
ALTER TABLE review ADD CONSTRAINT fk_review_service_request FOREIGN KEY (service_request_id) REFERENCES service_request(id);
