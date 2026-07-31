package com.enterprise.erp.clients.infrastructure.persistence;

import com.enterprise.erp.clients.domain.ClientType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ClientTypeConverter implements AttributeConverter<ClientType, String> {

    @Override
    public String convertToDatabaseColumn(ClientType attribute) {
        if (attribute == null) return null;
        return switch (attribute) {
            case PERSON -> "NATURAL_PERSON";
            case COMPANY -> "COMPANY";
        };
    }

    @Override
    public ClientType convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return switch (dbData) {
            case "NATURAL_PERSON", "PERSON" -> ClientType.PERSON;
            case "COMPANY" -> ClientType.COMPANY;
            default -> throw new IllegalArgumentException("Unknown ClientType: " + dbData);
        };
    }
}
