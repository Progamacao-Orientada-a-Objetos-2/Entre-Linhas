--liquibase formatted sql

--changeset josewynder:006
CREATE UNIQUE INDEX ux_chat_unique_pair
    ON chat (LEAST(sender_id, receiver_id), GREATEST(sender_id, receiver_id));

CREATE INDEX idx_chat_sender_receiver
    ON chat (sender_id, receiver_id);
