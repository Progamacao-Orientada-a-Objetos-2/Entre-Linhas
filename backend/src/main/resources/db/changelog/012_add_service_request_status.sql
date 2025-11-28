--liquibase formatted sql

--changeset entrelinhas:012
ALTER TABLE service_request
ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'pending';
