package com.btg.fondos.application.fund.command;

import com.btg.fondos.application.cqrs.Command;

public record CancelSubscriptionCommand(String clientId, String fundId) implements Command {
}
