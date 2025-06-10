package com.example.messageserver.service;

import org.springframework.stereotype.Service;
import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.spec.X509EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Service
public class EncryptionService {
    private static final String CIPHER_INSTANCE = "RSA";
    
    public String encryptMessage(String message, String publicKeyStr) {
        try {
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            
            byte[] encryptedBytes = cipher.doFinal(message.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при шифровании сообщения", e);
        }
    }

    public String decryptHash(String encryptedDataB64, String publicKeyPEM) {
        try {
            // Очистка PEM заголовков
            String publicKeyClean = publicKeyPEM
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "");

            // Декодирование из Base64
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyClean);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);

            Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);

            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedDataB64));
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка расшифровки открытым ключом", e);
        }
    }
} 