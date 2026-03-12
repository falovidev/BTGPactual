package com.btg.fondos.application.login.handler;

import com.btg.fondos.application.login.command.LoginCommand;
import com.btg.fondos.application.cqrs.CommandHandler;
import com.btg.fondos.domain.auth.service.AuthDomainService;
import com.btg.fondos.domain.login.model.LoginResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginHandler implements CommandHandler<LoginCommand, LoginResult> {

    private final AuthDomainService authDomainService;

    @Override
    public LoginResult handle(LoginCommand command) {
        return authDomainService.login(command.email(), command.password());
    }
}
