package com.cofat.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TotpSetupResponse {
    private String qrCodeImage; // image base64 à afficher côté frontend
    private String secret;      // affiché en secours si le QR ne scanne pas
}