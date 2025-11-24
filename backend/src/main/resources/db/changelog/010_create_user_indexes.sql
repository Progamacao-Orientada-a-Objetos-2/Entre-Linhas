--liquibase formatted sql

--changeset josewynder:010
CREATE UNIQUE INDEX idx_user_account_cpf_unique
    ON user_account(cpf)
    WHERE cpf IS NOT NULL;

CREATE UNIQUE INDEX idx_user_account_cnpj_unique
    ON user_account(cnpj)
    WHERE cnpj IS NOT NULL;

CREATE UNIQUE INDEX idx_user_account_state_registration_unique
    ON user_account(state_registration)
    WHERE user_type = 'company' AND state_registration IS NOT NULL;

CREATE UNIQUE INDEX idx_user_account_phone_unique
    ON user_account(phone)
    WHERE phone IS NOT NULL;
