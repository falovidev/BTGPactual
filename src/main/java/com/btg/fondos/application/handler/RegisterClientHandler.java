package com.btg.fondos.application.handler;

import com.btg.fondos.application.command.RegisterClientCommand;
import com.btg.fondos.application.cqrs.CommandHandler;
import com.btg.fondos.domain.client.model.Client;
import com.btg.fondos.infrastructure.adapter.in.web.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegisterClientHandler implements CommandHandler<RegisterClientCommand, Client> {

    private final AuthService authService;

    @Override
    public Client handle(RegisterClientCommand command) {
        return authService.register(
                command.name(), command.email(), command.phone(),
                command.password(), command.notificationPreference());
    }
}
