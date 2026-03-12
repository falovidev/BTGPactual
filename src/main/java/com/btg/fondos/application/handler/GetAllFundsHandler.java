package com.btg.fondos.application.handler;

import com.btg.fondos.application.cqrs.QueryHandler;
import com.btg.fondos.application.query.GetAllFundsQuery;
import com.btg.fondos.domain.fund.model.Fund;
import com.btg.fondos.domain.fund.port.FundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllFundsHandler implements QueryHandler<GetAllFundsQuery, List<Fund>> {

    private final FundRepository fundRepository;

    @Override
    public List<Fund> handle(GetAllFundsQuery query) {
        return fundRepository.findAll();
    }
}
