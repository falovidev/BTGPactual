package com.btg.fondos.domain.port.in;

import com.btg.fondos.domain.model.Client;
import com.btg.fondos.domain.model.NotificationType;

public interface AuthUseCase {
    Client register(String name, String email, String phone,
                    String password, NotificationType notificationPreference);
    AuthResult login(String email, String password);

    record AuthResult(String token, Client client) {}
}
