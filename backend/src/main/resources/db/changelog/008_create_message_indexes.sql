--liquibase formatted sql

--changeset josewynder:008
CREATE INDEX idx_message_chat_id ON message(chat_id);
CREATE INDEX idx_message_sender_id ON message(sender_id);
CREATE INDEX idx_message_created_at ON message(created_at);
