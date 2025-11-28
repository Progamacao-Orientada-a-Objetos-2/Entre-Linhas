package com.entrelinhas.backend.domain.converter;

import com.entrelinhas.backend.domain.UserType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UserTypeConverter implements AttributeConverter<UserType, String> {

    @Override
    public String convertToDatabaseColumn(UserType attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public UserType convertToEntityAttribute(String dbData) {
        return dbData != null ? UserType.fromValue(dbData) : null;
    }
}
