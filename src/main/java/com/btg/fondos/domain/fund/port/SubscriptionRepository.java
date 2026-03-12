package com.btg.fondos.domain.fund.port;

import com.btg.fondos.domain.fund.model.Subscription;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository {
    Subscription save(Subscription subscription);
    void delete(String clientId, String fundId);
    Optional<Subscription> findByClientIdAndFundId(String clientId, String fundId);
    List<Subscription> findByClientId(String clientId);
}
