package com.btg.fondos.domain.client.model;

import com.btg.fondos.domain.client.exception.InsufficientBalanceException;
import com.btg.fondos.domain.notification.model.NotificationType;

import java.math.BigDecimal;

public class Client {

    private final String clientId;
    private final String name;
    private final String email;
    private final String phone;
    private BigDecimal balance;
    private final NotificationType notificationPreference;
    private final String password;
    private final Role role;

    public Client(String clientId, String name, String email, String phone,
                  BigDecimal balance, NotificationType notificationPreference,
                  String password, Role role) {
        this.clientId = clientId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.balance = balance;
        this.notificationPreference = notificationPreference;
        this.password = password;
        this.role = role;
    }

    //region Comportamiento de dominio

    public void debit(BigDecimal amount, String fundName) {
        if (this.balance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException(fundName);
        }
        this.balance = this.balance.subtract(amount);
    }

    public void refund(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    //endregion

    //region Getters

    public String getClientId() { return clientId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public BigDecimal getBalance() { return balance; }
    public NotificationType getNotificationPreference() { return notificationPreference; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }

    //endregion
}
