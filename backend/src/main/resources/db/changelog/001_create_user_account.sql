--liquibase formatted sql

--changeset josewynder:001
CREATE TABLE user_account (
    id BIGSERIAL PRIMARY KEY,
    user_type VARCHAR(20) NOT NULL CHECK (user_type IN ('faccionista', 'faction', 'company')),
    full_name VARCHAR(255) NOT NULL,
    cpf VARCHAR(14),
    birth_date DATE,
    cnpj VARCHAR(18),
    state_registration VARCHAR(50),
    profile_image_url VARCHAR(500),
    description TEXT,
    email VARCHAR(255) UNIQUE NOT NULL,
    email_verified BOOLEAN DEFAULT FALSE NOT NULL,
    phone VARCHAR(20),
    state VARCHAR(100),
    city VARCHAR(100),
    main_service_id BIGINT,
    password_hash VARCHAR(255) NOT NULL,
    availability BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted_at TIMESTAMP NULL,

    CHECK (
        (user_type <> 'faccionista') OR
        (cpf IS NOT NULL AND birth_date IS NOT NULL AND cnpj IS NULL AND state_registration IS NULL)
    ),
    CHECK (
        (user_type <> 'company') OR
        (cnpj IS NOT NULL AND state_registration IS NOT NULL AND cpf IS NULL AND birth_date IS NULL)
    ),
    CHECK (
        (user_type <> 'faction') OR
        (cnpj IS NOT NULL AND state_registration IS NULL AND cpf IS NULL AND birth_date IS NULL)
    )
);
