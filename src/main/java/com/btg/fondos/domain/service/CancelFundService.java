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
public class CancelFundService {

    private final FundRepository fundRepository;
    private final ClientRepository clientRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final TransactionRepository transactionRepository;

    public Transaction cancel(String clientId, String fundId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(clientId));

        Fund fund = fundRepository.findById(fundId)
                .orElseThrow(() -> new FundNotFoundException(fundId));

        Subscription subscription = subscriptionRepository.findByClientIdAndFundId(clientId, fundId)
                .orElseThrow(() -> new SubscriptionNotFoundException(fund.getName()));

        client.setBalance(client.getBalance().add(subscription.getAmount()));
        clientRepository.save(client);

        subscriptionRepository.delete(clientId, fundId);

        Transaction transaction = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .clientId(clientId)
                .fundId(fundId)
                .fundName(fund.getName())
                .type(TransactionType.CANCELLATION)
                .amount(subscription.getAmount())
                .timestamp(Instant.now())
                .build();
        transactionRepository.save(transaction);

        log.info("Cliente {} canceló suscripción al fondo {}. Monto devuelto: {}",
                clientId, fund.getName(), subscription.getAmount());
        return transaction;
    }
}
