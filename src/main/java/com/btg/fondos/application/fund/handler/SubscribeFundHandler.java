package com.btg.fondos.application.fund.handler;

import com.btg.fondos.application.fund.command.SubscribeFundCommand;
import com.btg.fondos.application.cqrs.CommandHandler;
import com.btg.fondos.domain.transaction.model.Transaction;
import com.btg.fondos.domain.fund.service.SubscribeFundService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscribeFundHandler implements CommandHandler<SubscribeFundCommand, Transaction> {

    private final SubscribeFundService subscribeFundService;

    @Override
    public Transaction handle(SubscribeFundCommand command) {
        return subscribeFundService.subscribe(command.clientId(), command.fundId());
    }
}
