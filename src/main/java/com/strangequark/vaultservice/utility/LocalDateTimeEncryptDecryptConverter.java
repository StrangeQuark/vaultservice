package com.strangequark.vaultservice.utility;

import jakarta.persistence.AttributeConverter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeEncryptDecryptConverter implements AttributeConverter<LocalDateTime, String> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public String convertToDatabaseColumn(LocalDateTime attribute) {
        if (attribute == null) return null;
        String formatted = attribute.format(FORMATTER);
        return EncryptionUtility.encrypt(formatted);
    }

    @Override
    public LocalDateTime convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        String decrypted = EncryptionUtility.decrypt(dbData);
        return LocalDateTime.parse(decrypted, FORMATTER);
    }
}
