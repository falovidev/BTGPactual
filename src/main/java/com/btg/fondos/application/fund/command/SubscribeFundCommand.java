package com.btg.fondos.application.fund.command;

import com.btg.fondos.application.cqrs.Command;

public record SubscribeFundCommand(String clientId, String fundId) implements Command {
}
