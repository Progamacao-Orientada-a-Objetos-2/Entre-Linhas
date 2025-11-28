--liquibase formatted sql

--changeset josewynder:011
ALTER TABLE user_account
ADD CONSTRAINT fk_user_main_service
FOREIGN KEY (main_service_id) REFERENCES service(id)
ON DELETE SET NULL;