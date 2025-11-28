package com.entrelinhas.backend.domain.converter;

import com.entrelinhas.backend.domain.TokenType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TokenTypeConverter implements AttributeConverter<TokenType, String> {
    @Override
    public String convertToDatabaseColumn(TokenType attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public TokenType convertToEntityAttribute(String dbData) {
        return dbData != null ? TokenType.fromValue(dbData) : null;
    }
}
