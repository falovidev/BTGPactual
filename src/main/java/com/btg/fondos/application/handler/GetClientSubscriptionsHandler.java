package com.btg.fondos.application.handler;

import com.btg.fondos.application.cqrs.QueryHandler;
import com.btg.fondos.application.query.GetClientSubscriptionsQuery;
import com.btg.fondos.domain.model.Subscription;
import com.btg.fondos.domain.service.FundService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetClientSubscriptionsHandler implements QueryHandler<GetClientSubscriptionsQuery, List<Subscription>> {

    private final FundService fundService;

    @Override
    public List<Subscription> handle(GetClientSubscriptionsQuery query) {
        return fundService.getClientSubscriptions(query.clientId());
    }
}
