package com.btg.fondos.domain.fund.service;

import com.btg.fondos.domain.client.exception.ClientNotFoundException;
import com.btg.fondos.domain.client.model.Client;
import com.btg.fondos.domain.client.port.ClientRepository;
import com.btg.fondos.domain.fund.exception.FundNotFoundException;
import com.btg.fondos.domain.fund.exception.SubscriptionNotFoundException;
import com.btg.fondos.domain.fund.model.Fund;
import com.btg.fondos.domain.fund.model.Subscription;
import com.btg.fondos.domain.common.port.UnitOfWorkFactory;
import com.btg.fondos.domain.fund.port.FundRepository;
import com.btg.fondos.domain.fund.port.SubscriptionRepository;
import com.btg.fondos.domain.transaction.model.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CancelFundService {

    private final FundRepository fundRepository;
    private final ClientRepository clientRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UnitOfWorkFactory unitOfWorkFactory;

    public Transaction cancel(String clientId, String fundId) {
        Client client = findClient(clientId);
        Fund fund = findFund(fundId);
        Subscription subscription = findActiveSubscription(clientId, fundId, fund.getName());

        client.refund(subscription.getAmount());

        Transaction transaction = Transaction.createCancellation(clientId, fund, subscription.getAmount());

        unitOfWorkFactory.create()
                .save(client)
                .delete(Subscription.class, clientId, fundId)
                .save(transaction)
                .commit();

        log.info("Cliente {} canceló suscripción al fondo {}. Monto devuelto: {}",
                clientId, fund.getName(), subscription.getAmount());
        return transaction;
    }

    //region Métodos privados

    private Client findClient(String clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(clientId));
    }

    private Fund findFund(String fundId) {
        return fundRepository.findById(fundId)
                .orElseThrow(() -> new FundNotFoundException(fundId));
    }

    private Subscription findActiveSubscription(String clientId, String fundId, String fundName) {
        return subscriptionRepository.findByClientIdAndFundId(clientId, fundId)
                .orElseThrow(() -> new SubscriptionNotFoundException(fundName));
    }

    //endregion
}
