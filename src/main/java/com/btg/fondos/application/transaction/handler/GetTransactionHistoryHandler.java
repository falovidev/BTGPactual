package com.btg.fondos.application.transaction.handler;

import com.btg.fondos.application.cqrs.QueryHandler;
import com.btg.fondos.application.transaction.query.GetTransactionHistoryQuery;
import com.btg.fondos.domain.transaction.model.Transaction;
import com.btg.fondos.domain.transaction.service.GetTransactionHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetTransactionHistoryHandler implements QueryHandler<GetTransactionHistoryQuery, List<Transaction>> {

    private final GetTransactionHistoryService getTransactionHistoryService;

    @Override
    public List<Transaction> handle(GetTransactionHistoryQuery query) {
        return getTransactionHistoryService.execute(query.clientId());
    }
}
