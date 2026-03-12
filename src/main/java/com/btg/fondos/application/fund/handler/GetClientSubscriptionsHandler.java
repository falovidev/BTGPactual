package com.btg.fondos.application.fund.handler;

import com.btg.fondos.application.cqrs.QueryHandler;
import com.btg.fondos.application.fund.query.GetClientSubscriptionsQuery;
import com.btg.fondos.domain.fund.model.Subscription;
import com.btg.fondos.domain.fund.port.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetClientSubscriptionsHandler implements QueryHandler<GetClientSubscriptionsQuery, List<Subscription>> {

    private final SubscriptionRepository subscriptionRepository;

    @Override
    public List<Subscription> handle(GetClientSubscriptionsQuery query) {
        return subscriptionRepository.findByClientId(query.clientId());
    }
}
