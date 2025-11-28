--liquibase formatted sql

--changeset entrelinhas:013
ALTER TABLE user_account
ADD COLUMN last_access_at TIMESTAMP NULL;
