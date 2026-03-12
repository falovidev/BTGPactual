package com.btg.fondos.application.login.command;

import com.btg.fondos.application.cqrs.Command;

public record LoginCommand(String email, String password) implements Command {
}
