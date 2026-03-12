package com.btg.fondos.application.command;

import com.btg.fondos.application.cqrs.Command;
import com.btg.fondos.domain.model.NotificationType;

public record RegisterClientCommand(
        String name,
        String email,
        String phone,
        String password,
        NotificationType notificationPreference
) implements Command {
}
