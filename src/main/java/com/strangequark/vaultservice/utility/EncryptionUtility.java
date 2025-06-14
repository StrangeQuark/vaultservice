package com.strangequark.vaultservice.utility;

import com.strangequark.vaultservice.variable.Variable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.List;

@Component
public class EncryptionUtility {

    private String ALGORITHM = "AES";

    @Value("${ENCRYPTION_KEY}")
    private String ENCRYPTION_KEY;

    public String encrypt(String plainText) throws Exception {
        SecretKey key = new SecretKeySpec(ENCRYPTION_KEY.getBytes(), ALGORITHM);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());

        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public String decrypt(String encryptedText) throws Exception {
        SecretKey key = new SecretKeySpec(ENCRYPTION_KEY.getBytes(), ALGORITHM);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);

        return new String(decryptedBytes);
    }

    public List<Variable> decryptList(List<Variable> encryptedVars) throws Exception {
        for(Variable v : encryptedVars) {
            v.setKey(decrypt(v.getKey()));
            v.setValue(decrypt(v.getValue()));
        }

        return encryptedVars;
    }
}
