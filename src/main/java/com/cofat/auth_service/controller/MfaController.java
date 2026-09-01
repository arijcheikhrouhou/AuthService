package com.cofat.auth_service.controller;

import com.cofat.auth_service.dto.TotpSetupResponse;
import com.cofat.auth_service.dto.TotpVerifyRequest;
import com.cofat.auth_service.entity.User;
import com.cofat.auth_service.repository.UserRepository;
import com.cofat.auth_service.security.TotpService;
import com.cofat.auth_service.security.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/mfa")
public class MfaController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TotpService totpService;

    // Étape 1 : générer un secret + QR code (l'utilisateur doit être connecté)
    @PostMapping("/setup")
    public ResponseEntity<?> setupMfa(Authentication authentication) throws Exception {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        String secret = totpService.generateSecret();
        String qrImage = totpService.generateQrCodeImage(secret, user.getUsername());

        // On stocke le secret mais on n'active PAS encore le MFA
        // (activation seulement après vérification réussie du premier code)
        user.setMfaSecret(secret);
        userRepository.save(user);

        return ResponseEntity.ok(new TotpSetupResponse(qrImage, secret));
    }

    // Étape 2 : confirmer le premier code pour activer réellement le MFA
    @PostMapping("/enable")
    public ResponseEntity<?> enableMfa(Authentication authentication, @Valid @RequestBody TotpVerifyRequest request) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        if (user.getMfaSecret() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Aucune configuration MFA en cours. Lancez /setup d'abord."));
        }

        boolean valid = totpService.verifyCode(user.getMfaSecret(), request.getCode());
        if (!valid) {
            return ResponseEntity.status(400).body(Map.of("message", "Code invalide."));
        }

        user.setMfaEnabled(true);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "MFA activé avec succès."));
    }

    // Désactiver le MFA (nécessite d'être connecté)
    @PostMapping("/disable")
    public ResponseEntity<?> disableMfa(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        user.setMfaEnabled(false);
        user.setMfaSecret(null);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "MFA désactivé."));
    }
}