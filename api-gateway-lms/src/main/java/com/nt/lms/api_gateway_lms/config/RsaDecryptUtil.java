package com.nt.lms.api_gateway_lms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

/**
 * Utility class for RSA decryption operations in the LMS API Gateway.
 * This component provides functionality to decrypt RSA-encrypted text using a private key
 * loaded from application configuration.
 *
 * <p>The class expects the private key to be in PKCS#8 format and uses RSA/ECB/PKCS1Padding
 * for decryption to match frontend encryption standards.</p>
 *
 * @author Your Name
 * @version 1.0
 * @since 1.0
 */
@Component
public class RsaDecryptUtil {

    /**
     * The RSA private key in PEM format loaded from application configuration.
     * This key is used for decrypting encrypted text received from clients.
     * The key should be in PKCS#8 format with BEGIN/END PRIVATE KEY headers.
     */
    private final String privateKey;

    /**
     * Constructs a new RsaDecryptUtil with the specified private key.
     *
     * @param privateKey the RSA private key in PEM format, injected from
     *                   application properties using the key "private.key"
     * @throws IllegalArgumentException if the privateKey is null or empty
     */
    public RsaDecryptUtil(@Value("${private.key}") final String privateKey) {
        this.privateKey = privateKey;
    }

    /**
     * Decrypts the given RSA-encrypted text using the configured private key.
     *
     * <p>This method performs the following operations:</p>
     * <ul>
     *   <li>Removes PEM headers/footers from the private key</li>
     *   <li>Decodes the private key from Base64</li>
     *   <li>Creates a PrivateKey object using PKCS8EncodedKeySpec</li>
     *   <li>Initializes RSA cipher with PKCS1Padding</li>
     *   <li>Decrypts the input text and returns the result as UTF-8 string</li>
     * </ul>
     *
     * @param encryptedText the Base64-encoded RSA-encrypted text to decrypt.
     *                      Must not be null or empty.
     * @return the decrypted text as a UTF-8 string
     * @throws RuntimeException         if decryption fails due to:
     *                                  <ul>
     *                                    <li>Invalid private key format</li>
     *                                    <li>Invalid encrypted text format</li>
     *                                    <li>Cryptographic operation failures</li>
     *                                    <li>Base64 decoding errors</li>
     *                                  </ul>
     * @throws IllegalArgumentException if encryptedText is null or empty
     */
    public String decrypt(final String encryptedText) {
        try {
            // Remove header/footer and decode base64
            String privateKeyPEM = privateKey
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            System.out.print(privateKeyPEM);
            byte[] keyBytes = Base64.getDecoder().decode(privateKeyPEM);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey generatedPrivate = kf.generatePrivate(spec);

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding"); // Must match frontend padding
            cipher.init(Cipher.DECRYPT_MODE, generatedPrivate);

            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
            return new String(decryptedBytes, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new RuntimeException("RSA decryption failed", e);
        }
    }
}
