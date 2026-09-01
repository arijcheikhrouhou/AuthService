package com.cofat.auth_service.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SecurityLogger {

    private static final Logger log = LoggerFactory.getLogger("SECURITY");

    public void loginFailed(String username, String ip) {
        log.info("LOGIN_FAILED | username={} | ip={}", username, ip);
    }

    public void loginSuccess(String username, String ip) {
        log.info("LOGIN_SUCCESS | username={} | ip={}", username, ip);
    }

    public void rateLimitBlocked(String ip) {
        log.info("RATE_LIMIT_BLOCKED | ip={}", ip);
    }

    public void mfaFailed(String username, String ip) {
        log.info("MFA_FAILED | username={} | ip={}", username, ip);
    }

    public void accountDisabledAttempt(String username, String ip) {
        log.info("ACCOUNT_DISABLED_LOGIN_ATTEMPT | username={} | ip={}", username, ip);
    }

    public void adminAccessDenied(String username, String ip, String path) {
        log.info("ADMIN_ACCESS_DENIED | username={} | ip={} | path={}", username, ip, path);
    }
}