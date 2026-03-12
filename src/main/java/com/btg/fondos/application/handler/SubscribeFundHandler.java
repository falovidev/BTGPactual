package com.btg.fondos.application.handler;

import com.btg.fondos.application.command.SubscribeFundCommand;
import com.btg.fondos.application.cqrs.CommandHandler;
import com.btg.fondos.domain.model.Transaction;
import com.btg.fondos.domain.service.FundService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscribeFundHandler implements CommandHandler<SubscribeFundCommand, Transaction> {

    private final FundService fundService;

    @Override
    public Transaction handle(SubscribeFundCommand command) {
        return fundService.subscribe(command.clientId(), command.fundId());
    }
}
