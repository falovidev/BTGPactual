package com.btg.fondos.domain.service;

import com.btg.fondos.domain.exception.*;
import com.btg.fondos.domain.model.*;
import com.btg.fondos.domain.port.out.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

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
        validateSufficientBalance(client, fund);

        debitBalance(client, fund.getMinimumAmount());
        createSubscription(clientId, fund);
        Transaction transaction = createTransaction(clientId, fund);

        sendNotification(client, fund);

        log.info("Cliente {} suscrito al fondo {} por {}", clientId, fund.getName(), fund.getMinimumAmount());
        return transaction;
    }

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

    private void validateSufficientBalance(Client client, Fund fund) {
        if (client.getBalance().compareTo(fund.getMinimumAmount()) < 0) {
            throw new InsufficientBalanceException(fund.getName());
        }
    }

    private void debitBalance(Client client, java.math.BigDecimal amount) {
        client.setBalance(client.getBalance().subtract(amount));
        clientRepository.save(client);
    }

    private void createSubscription(String clientId, Fund fund) {
        Subscription subscription = Subscription.builder()
                .clientId(clientId)
                .fundId(fund.getFundId())
                .fundName(fund.getName())
                .amount(fund.getMinimumAmount())
                .subscribedAt(Instant.now())
                .build();
        subscriptionRepository.save(subscription);
    }

    private Transaction createTransaction(String clientId, Fund fund) {
        Transaction transaction = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .clientId(clientId)
                .fundId(fund.getFundId())
                .fundName(fund.getName())
                .type(TransactionType.OPENING)
                .amount(fund.getMinimumAmount())
                .timestamp(Instant.now())
                .build();
        transactionRepository.save(transaction);
        return transaction;
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
}
