package com.cofat.auth_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateRolesRequest {
    private List<String> roles; // ex: ["ROLE_USER", "ROLE_ADMIN"]
}