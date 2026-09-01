package com.cofat.auth_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TotpVerifyRequest {
    @NotBlank
    private String code;
}