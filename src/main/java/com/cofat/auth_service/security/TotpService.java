package com.cofat.auth_service.security;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class TotpService {

    private final SecretGenerator secretGenerator = new DefaultSecretGenerator();
    private final QrGenerator qrGenerator = new ZxingPngQrGenerator();
    private final TimeProvider timeProvider = new SystemTimeProvider();
    private final CodeGenerator codeGenerator = new DefaultCodeGenerator();
    private final CodeVerifier codeVerifier = new DefaultCodeVerifier(codeGenerator, timeProvider);

    // Génère un nouveau secret aléatoire pour un utilisateur
    public String generateSecret() {
        return secretGenerator.generate();
    }

    // Génère l'image QR code (en base64) à scanner avec Google Authenticator / Microsoft Authenticator
    public String generateQrCodeImage(String secret, String username) throws Exception {
        QrData data = new QrData.Builder()
                .label(username)
                .secret(secret)
                .issuer("CofatAuth")
                .algorithm(HashingAlgorithm.SHA1)
                .digits(6)
                .period(30)
                .build();

        byte[] imageBytes = qrGenerator.generate(data);
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    // Vérifie qu'un code à 6 chiffres saisi par l'utilisateur correspond au secret
    public boolean verifyCode(String secret, String code) {
        return codeVerifier.isValidCode(secret, code);
    }
}