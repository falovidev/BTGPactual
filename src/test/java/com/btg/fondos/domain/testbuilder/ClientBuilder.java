package com.btg.fondos.domain.testbuilder;

import com.btg.fondos.domain.client.model.Client;
import com.btg.fondos.domain.client.model.Role;
import com.btg.fondos.domain.notification.model.NotificationType;

import java.math.BigDecimal;

public class ClientBuilder {

    private String clientId = "client-1";
    private String name = "Juan Pérez";
    private String email = "juan@test.com";
    private String phone = "+573001234567";
    private BigDecimal balance = new BigDecimal("500000");
    private NotificationType notificationPreference = NotificationType.EMAIL;
    private String password = "hashed-pass";
    private Role role = Role.USER;

    public static ClientBuilder aClient() {
        return new ClientBuilder();
    }

    public ClientBuilder withClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public ClientBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ClientBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public ClientBuilder withPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public ClientBuilder withBalance(BigDecimal balance) {
        this.balance = balance;
        return this;
    }

    public ClientBuilder withBalance(String balance) {
        this.balance = new BigDecimal(balance);
        return this;
    }

    public ClientBuilder withNotificationPreference(NotificationType notificationPreference) {
        this.notificationPreference = notificationPreference;
        return this;
    }

    public ClientBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public ClientBuilder withRole(Role role) {
        this.role = role;
        return this;
    }

    public Client build() {
        return new Client(clientId, name, email, phone, balance, notificationPreference, password, role);
    }
}
