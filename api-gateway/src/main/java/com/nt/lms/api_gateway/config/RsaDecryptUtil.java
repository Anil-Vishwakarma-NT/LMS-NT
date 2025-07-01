package com.nt.lms.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Component
public class RsaDecryptUtil {

    private  final String privateKey;

    public RsaDecryptUtil(@Value("${private.key}") String privateKey) {
        this.privateKey = privateKey;
    }
//public RsaDecryptUtil(@Value("${private.key}") String privateKey) {
//    this.PRIVATE_KEY = privateKey;
//}
//

    public  String decrypt(String encryptedText) {
        try {
            // Remove header/footer and decode base64
            String privateKeyPEM = privateKey
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");
            System.out.print(privateKeyPEM);
            byte[] keyBytes = Base64.getDecoder().decode(privateKeyPEM);
            System.out.println("keybytes generated");
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            System.out.println("spec for keys");
            KeyFactory kf = KeyFactory.getInstance("RSA");
            System.out.println("key factory instance");
            PrivateKey privateKey = kf.generatePrivate(spec);
            System.out.println("privatekey");
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding"); // Must match frontend padding
            System.out.println("cipher");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            System.out.println("cyper inint hit");
            System.out.println("Encrypted input: " + encryptedText);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
//            System.out.println("Encrypted input: " + encryptedText);
            System.out.println("decrypted bytes");
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("RSA decryption failed", e);
        }
    }
}
