package com.btg.fondos.domain.notification.port;

import com.btg.fondos.domain.client.model.Client;

public interface NotificationPort {
    void sendNotification(Client client, String subject, String message);
}
