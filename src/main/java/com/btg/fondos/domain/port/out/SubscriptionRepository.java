package com.btg.fondos.domain.port.out;

import com.btg.fondos.domain.model.Subscription;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository {
    Subscription save(Subscription subscription);
    void delete(String clientId, String fundId);
    Optional<Subscription> findByClientIdAndFundId(String clientId, String fundId);
    List<Subscription> findByClientId(String clientId);
}
