package com.btg.fondos.application.handler;

import com.btg.fondos.application.command.CancelSubscriptionCommand;
import com.btg.fondos.application.cqrs.CommandHandler;
import com.btg.fondos.domain.model.Transaction;
import com.btg.fondos.domain.service.CancelFundService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CancelSubscriptionHandler implements CommandHandler<CancelSubscriptionCommand, Transaction> {

    private final CancelFundService cancelFundService;

    @Override
    public Transaction handle(CancelSubscriptionCommand command) {
        return cancelFundService.cancel(command.clientId(), command.fundId());
    }
}
