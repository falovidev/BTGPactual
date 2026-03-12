package com.btg.fondos.application.handler;

import com.btg.fondos.application.cqrs.QueryHandler;
import com.btg.fondos.application.query.GetTransactionHistoryQuery;
import com.btg.fondos.domain.model.Transaction;
import com.btg.fondos.domain.service.FundService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetTransactionHistoryHandler implements QueryHandler<GetTransactionHistoryQuery, List<Transaction>> {

    private final FundService fundService;

    @Override
    public List<Transaction> handle(GetTransactionHistoryQuery query) {
        return fundService.getTransactionHistory(query.clientId());
    }
}
