package com.btg.fondos.application.handler;

import com.btg.fondos.application.command.LoginCommand;
import com.btg.fondos.application.cqrs.CommandHandler;
import com.btg.fondos.domain.model.LoginResult;
import com.btg.fondos.infrastructure.adapter.in.web.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginHandler implements CommandHandler<LoginCommand, LoginResult> {

    private final AuthService authService;

    @Override
    public LoginResult handle(LoginCommand command) {
        return authService.login(command.email(), command.password());
    }
}
