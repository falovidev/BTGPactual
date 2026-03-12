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
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(clientId));

        Fund fund = fundRepository.findById(fundId)
                .orElseThrow(() -> new FundNotFoundException(fundId));

        subscriptionRepository.findByClientIdAndFundId(clientId, fundId)
                .ifPresent(s -> { throw new DuplicateSubscriptionException(fund.getName()); });

        if (client.getBalance().compareTo(fund.getMinimumAmount()) < 0) {
            throw new InsufficientBalanceException(fund.getName());
        }

        client.setBalance(client.getBalance().subtract(fund.getMinimumAmount()));
        clientRepository.save(client);

        Subscription subscription = Subscription.builder()
                .clientId(clientId)
                .fundId(fundId)
                .fundName(fund.getName())
                .amount(fund.getMinimumAmount())
                .subscribedAt(Instant.now())
                .build();
        subscriptionRepository.save(subscription);

        Transaction transaction = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .clientId(clientId)
                .fundId(fundId)
                .fundName(fund.getName())
                .type(TransactionType.OPENING)
                .amount(fund.getMinimumAmount())
                .timestamp(Instant.now())
                .build();
        transactionRepository.save(transaction);

        sendNotification(client, fund);

        log.info("Cliente {} suscrito al fondo {} por {}", clientId, fund.getName(), fund.getMinimumAmount());
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
