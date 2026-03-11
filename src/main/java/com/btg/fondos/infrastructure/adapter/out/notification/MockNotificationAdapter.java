package com.btg.fondos.infrastructure.adapter.out.notification;

import com.btg.fondos.domain.model.Client;
import com.btg.fondos.domain.port.out.NotificationPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "aws.sns.enabled", havingValue = "false", matchIfMissing = true)
public class MockNotificationAdapter implements NotificationPort {

    @Override
    public void sendNotification(Client client, String subject, String message) {
        log.info("[MOCK NOTIFICATION] Tipo: {} | Destinatario: {} | Asunto: {} | Mensaje: {}",
                client.getNotificationPreference(),
                getDestination(client),
                subject,
                message);
    }

    private String getDestination(Client client) {
        return switch (client.getNotificationPreference()) {
            case EMAIL -> client.getEmail();
            case SMS -> client.getPhone();
        };
    }
}
