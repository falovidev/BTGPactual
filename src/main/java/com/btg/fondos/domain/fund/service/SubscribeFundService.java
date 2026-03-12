package com.btg.fondos.domain.fund.service;

import com.btg.fondos.domain.client.exception.ClientNotFoundException;
import com.btg.fondos.domain.client.model.Client;
import com.btg.fondos.domain.client.port.ClientRepository;
import com.btg.fondos.domain.fund.exception.DuplicateSubscriptionException;
import com.btg.fondos.domain.fund.exception.FundNotFoundException;
import com.btg.fondos.domain.fund.model.Fund;
import com.btg.fondos.domain.fund.model.Subscription;
import com.btg.fondos.domain.fund.port.FundRepository;
import com.btg.fondos.domain.fund.port.SubscriptionRepository;
import com.btg.fondos.domain.notification.port.NotificationPort;
import com.btg.fondos.domain.transaction.model.Transaction;
import com.btg.fondos.domain.transaction.port.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscribeFundService {

    private final FundRepository fundRepository;
    private final ClientRepository clientRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final TransactionRepository transactionRepository;
    private final NotificationPort notificationPort;

    public Transaction subscribe(String clientId, String fundId) {
        Client client = findClient(clientId);
        Fund fund = findFund(fundId);

        validateNoDuplicateSubscription(clientId, fundId, fund.getName());

        client.debit(fund.getMinimumAmount(), fund.getName());
        clientRepository.save(client);

        Subscription subscription = Subscription.create(clientId, fund);
        subscriptionRepository.save(subscription);

        Transaction transaction = Transaction.createOpening(clientId, fund);
        transactionRepository.save(transaction);

        sendNotification(client, fund);

        log.info("Cliente {} suscrito al fondo {} por {}", clientId, fund.getName(), fund.getMinimumAmount());
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

    private void validateNoDuplicateSubscription(String clientId, String fundId, String fundName) {
        subscriptionRepository.findByClientIdAndFundId(clientId, fundId)
                .ifPresent(s -> { throw new DuplicateSubscriptionException(fundName); });
    }

    private void sendNotification(Client client, Fund fund) {
        String subject = "Suscripción exitosa - BTG Pactual";
        String message = String.format(
                "Estimado(a) %s, se ha suscrito exitosamente al fondo %s por un monto de COP $%s.",
                client.getName(), fund.getName(), fund.getMinimumAmount().toPlainString());

        try {
            notificationPort.sendNotification(client, subject, message);
        } catch (Exception e) {
            log.error("Error enviando notificación al cliente {}: {}", client.getClientId(), e.getMessage());
        }
    }

    //endregion
}
