package com.btg.fondos.application.handler;

import com.btg.fondos.application.cqrs.QueryHandler;
import com.btg.fondos.application.query.GetClientSubscriptionsQuery;
import com.btg.fondos.domain.model.Subscription;
import com.btg.fondos.domain.port.out.SubscriptionRepository;
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
