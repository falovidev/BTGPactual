package com.btg.fondos.application.client.handler;

import com.btg.fondos.application.client.command.RegisterClientCommand;
import com.btg.fondos.application.cqrs.CommandHandler;
import com.btg.fondos.domain.auth.service.AuthDomainService;
import com.btg.fondos.domain.client.model.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegisterClientHandler implements CommandHandler<RegisterClientCommand, Client> {

    private final AuthDomainService authDomainService;

    @Override
    public Client handle(RegisterClientCommand command) {
        return authDomainService.register(
                command.name(), command.email(), command.phone(),
                command.password(), command.notificationPreference());
    }
}
