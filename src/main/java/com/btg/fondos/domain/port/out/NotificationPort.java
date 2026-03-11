package com.btg.fondos.domain.port.out;

import com.btg.fondos.domain.model.Client;

public interface NotificationPort {
    void sendNotification(Client client, String subject, String message);
}
