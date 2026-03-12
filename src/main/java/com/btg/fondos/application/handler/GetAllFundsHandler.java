package com.btg.fondos.application.handler;

import com.btg.fondos.application.cqrs.QueryHandler;
import com.btg.fondos.application.query.GetAllFundsQuery;
import com.btg.fondos.domain.model.Fund;
import com.btg.fondos.domain.service.FundService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllFundsHandler implements QueryHandler<GetAllFundsQuery, List<Fund>> {

    private final FundService fundService;

    @Override
    public List<Fund> handle(GetAllFundsQuery query) {
        return fundService.getAllFunds();
    }
}
