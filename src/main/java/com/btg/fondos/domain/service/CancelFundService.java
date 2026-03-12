package com.btg.fondos.domain.service;

import com.btg.fondos.domain.exception.*;
import com.btg.fondos.domain.model.*;
import com.btg.fondos.domain.port.out.*;
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
    private final TransactionRepository transactionRepository;

    public Transaction cancel(String clientId, String fundId) {
        Client client = findClient(clientId);
        Fund fund = findFund(fundId);
        Subscription subscription = findActiveSubscription(clientId, fundId, fund.getName());

        client.refund(subscription.getAmount());
        clientRepository.save(client);

        subscriptionRepository.delete(clientId, fundId);

        Transaction transaction = Transaction.createCancellation(clientId, fund, subscription.getAmount());
        transactionRepository.save(transaction);

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
