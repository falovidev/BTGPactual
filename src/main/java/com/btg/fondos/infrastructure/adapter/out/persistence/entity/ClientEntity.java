package com.btg.fondos.infrastructure.adapter.out.persistence.entity;

import com.btg.fondos.domain.client.model.Client;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

import java.math.BigDecimal;

@DynamoDbBean
public class ClientEntity {

    private String clientId;
    private String name;
    private String email;
    private String phone;
    private BigDecimal balance;
    private String notificationPreference;
    private String password;
    private String role;

    @DynamoDbPartitionKey
    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @DynamoDbSecondaryPartitionKey(indexNames = "email-index")
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public String getNotificationPreference() { return notificationPreference; }
    public void setNotificationPreference(String notificationPreference) { this.notificationPreference = notificationPreference; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public static ClientEntity fromDomain(Client client) {
        ClientEntity entity = new ClientEntity();
        entity.setClientId(client.getClientId());
        entity.setName(client.getName());
        entity.setEmail(client.getEmail());
        entity.setPhone(client.getPhone());
        entity.setBalance(client.getBalance());
        entity.setNotificationPreference(client.getNotificationPreference().name());
        entity.setPassword(client.getPassword());
        entity.setRole(client.getRole().name());
        return entity;
    }
}
