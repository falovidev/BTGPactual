package com.btg.fondos.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Client {
    private String clientId;
    private String name;
    private String email;
    private String phone;
    private BigDecimal balance;
    private NotificationType notificationPreference;
    private String password;
    private Role role;
}
