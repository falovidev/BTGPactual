package com.btg.fondos.application.cqrs;

public interface CommandHandler<C extends Command, R> {
    R handle(C command);
}
