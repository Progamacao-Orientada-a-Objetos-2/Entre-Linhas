--liquibase formatted sql

--changeset josewynder:005
CREATE TABLE chat (
    id BIGSERIAL PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT chk_chat_diff_participants
        CHECK (sender_id <> receiver_id),

    CONSTRAINT fk_chat_sender
        FOREIGN KEY (sender_id) REFERENCES user_account(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_chat_receiver
        FOREIGN KEY (receiver_id) REFERENCES user_account(id)
        ON DELETE CASCADE
);
