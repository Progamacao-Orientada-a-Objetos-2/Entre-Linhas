package com.entrelinhas.backend.domain.converter;

import com.entrelinhas.backend.domain.ServiceRequestStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ServiceRequestStatusConverter implements AttributeConverter<ServiceRequestStatus, String> {
    @Override
    public String convertToDatabaseColumn(ServiceRequestStatus attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public ServiceRequestStatus convertToEntityAttribute(String dbData) {
        return dbData != null ? ServiceRequestStatus.fromValue(dbData) : null;
    }
}
