package com.meztlitech.agrobitacora.service;

import com.meztlitech.agrobitacora.dto.LicenseSchoolRequest;
import com.meztlitech.agrobitacora.dto.LicenseSchoolResponse;
import com.meztlitech.agrobitacora.entity.License;
import com.meztlitech.agrobitacora.repository.LicenseRepository;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;


@Service
@RequiredArgsConstructor
@Log4j2
public class LicenseService {

    private final LicenseRepository licenseRepository;

    public java.util.List<License> findAll() {
        return licenseRepository.findAll();
    }

    public LicenseSchoolResponse generateLicense(LicenseSchoolRequest request) {
        try {
            String payload = decryptStrong(request.getKey());
            String[] parts = payload.split("\\|");
            if (parts.length < 5) {
                throw new IllegalArgumentException("Invalid payload format");
            }
            String[] decript = decryptInner(parts[0], parts[1], parts[2]).split("\\|");
            License license = new License();
            license.setDecryptedText(decript[1]);
            license.setIv(parts[0]);
            license.setKey(parts[1]);
            license.setCipherText(parts[2]);
            license.setStudentTotal(parts[3]);
            license.setSchoolName(parts[4]);
            license.setExpirationDate(decript[0]);
            log.info("Decrypted License: {}", license.toString());
            License saved = licenseRepository.save(license);
            LicenseSchoolResponse response = new LicenseSchoolResponse();
            response.setCipher(saved.getCipherText());
            response.setIv(saved.getIv());
            response.setKey(saved.getKey());
            response.setExpirationDate(license.getExpirationDate());
            return response;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing license key: " + e.getMessage());
        }
    }

    private static final byte[] KEY = "e5dK7v9x/A?D(G+KbPeShVmYq3t6w9z$".getBytes(StandardCharsets.UTF_8);
    private static final byte[] HMAC_KEY = "HmacSecretKeyForLicense12345".getBytes(StandardCharsets.UTF_8);

    private static String decryptStrong(String encrypted) throws Exception {
        String[] parts = encrypted.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid encrypted payload");
        }

        byte[] iv = Base64.getDecoder().decode(parts[0]);
        byte[] cipherBytes = Base64.getDecoder().decode(parts[1]);
        byte[] mac = Base64.getDecoder().decode(parts[2]);

        Mac hmac = Mac.getInstance("HmacSHA256");
        hmac.init(new SecretKeySpec(HMAC_KEY, "HmacSHA256"));
        byte[] computedMac = hmac.doFinal(concat(iv, cipherBytes));
        if (!java.security.MessageDigest.isEqual(mac, computedMac)) {
            throw new SecurityException("HMAC validation failed");
        }

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(KEY, "AES"), new IvParameterSpec(iv));
        byte[] plainBytes = cipher.doFinal(cipherBytes);
        return new String(plainBytes, StandardCharsets.UTF_8);
    }

    private static String decryptInner(String ivB64, String keyB64, String cipherB64) throws Exception {
        byte[] iv = Base64.getDecoder().decode(ivB64);
        byte[] key = Base64.getDecoder().decode(keyB64);
        byte[] cipherBytes = Base64.getDecoder().decode(cipherB64);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
        byte[] plainBytes = cipher.doFinal(cipherBytes);
        return new String(plainBytes, StandardCharsets.UTF_8);
    }

    private static byte[] concat(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

}
