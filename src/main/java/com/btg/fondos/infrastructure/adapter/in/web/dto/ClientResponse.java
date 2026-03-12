package com.btg.fondos.infrastructure.adapter.in.web.dto;

import com.btg.fondos.domain.client.model.Client;
import com.btg.fondos.domain.notification.model.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ClientResponse {
    private String clientId;
    private String name;
    private String email;
    private String phone;
    private BigDecimal balance;
    private NotificationType notificationPreference;

    public static ClientResponse from(Client c) {
        return ClientResponse.builder()
                .clientId(c.getClientId())
                .name(c.getName())
                .email(c.getEmail())
                .phone(c.getPhone())
                .balance(c.getBalance())
                .notificationPreference(c.getNotificationPreference())
                .build();
    }
}
