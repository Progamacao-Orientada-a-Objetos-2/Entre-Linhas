--liquibase formatted sql

--changeset josewynder:007
CREATE TABLE message (
    id BIGSERIAL PRIMARY KEY,
    chat_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_read BOOLEAN DEFAULT FALSE NOT NULL,

    CONSTRAINT fk_message_chat
        FOREIGN KEY (chat_id)
        REFERENCES chat(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_message_sender
        FOREIGN KEY (sender_id)
        REFERENCES user_account(id)
        ON DELETE CASCADE
);
