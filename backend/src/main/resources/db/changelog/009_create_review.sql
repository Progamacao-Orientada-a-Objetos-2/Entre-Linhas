--liquibase formatted sql

--changeset josewynder:009
CREATE TABLE review (
    id BIGSERIAL PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    rating SMALLINT CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT fk_review_sender
        FOREIGN KEY (sender_id) REFERENCES user_account(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_review_receiver
        FOREIGN KEY (receiver_id) REFERENCES user_account(id)
        ON DELETE CASCADE,

    CONSTRAINT unique_review_per_user_pair UNIQUE (sender_id, receiver_id),
    CHECK (sender_id <> receiver_id)
);
