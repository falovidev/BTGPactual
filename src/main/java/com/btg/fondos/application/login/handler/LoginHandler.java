package com.btg.fondos.application.login.handler;

import com.btg.fondos.application.login.command.LoginCommand;
import com.btg.fondos.application.cqrs.CommandHandler;
import com.btg.fondos.domain.login.model.LoginResult;
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
