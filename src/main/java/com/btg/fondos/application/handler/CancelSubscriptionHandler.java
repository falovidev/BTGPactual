package com.btg.fondos.application.handler;

import com.btg.fondos.application.command.CancelSubscriptionCommand;
import com.btg.fondos.application.cqrs.CommandHandler;
import com.btg.fondos.domain.model.Transaction;
import com.btg.fondos.domain.service.FundService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CancelSubscriptionHandler implements CommandHandler<CancelSubscriptionCommand, Transaction> {

    private final FundService fundService;

    @Override
    public Transaction handle(CancelSubscriptionCommand command) {
        return fundService.cancel(command.clientId(), command.fundId());
    }
}
