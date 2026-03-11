package com.btg.fondos.infrastructure.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String clientId;
    private String name;
    private String email;
}
