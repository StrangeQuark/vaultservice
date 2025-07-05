package com.strangequark.vaultservice.utility;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Converter(autoApply = false)
public class EncryptDecryptConverter implements AttributeConverter<String, String> {

    private static EncryptionUtility encryptionUtility;

    @Autowired
    private EncryptionUtility injectedEncryptionService;

    @PostConstruct
    public void init() {
        encryptionUtility = injectedEncryptionService;
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return attribute == null ? null : encryptionUtility.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return dbData == null ? null : encryptionUtility.decrypt(dbData);
    }
}
