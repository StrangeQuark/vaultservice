// Integration file: Auth

package com.strangequark.vaultservice.utility;

import com.strangequark.vaultservice.serviceuser.ServiceUserRole;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Converter(autoApply = false)
public class RoleEncryptDecryptConverter implements AttributeConverter<ServiceUserRole, String> {

    private static EncryptionService encryptionService;

    @Autowired
    private EncryptionService injectedEncryptionService;

    @PostConstruct
    public void init() {
        encryptionService = injectedEncryptionService;
    }

    @Override
    public String convertToDatabaseColumn(ServiceUserRole role) {
        if (role == null) return null;
        return encryptionService.encrypt(role.name());
    }

    @Override
    public ServiceUserRole convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        String decrypted = encryptionService.decrypt(dbData);
        return ServiceUserRole.valueOf(decrypted);
    }
}
