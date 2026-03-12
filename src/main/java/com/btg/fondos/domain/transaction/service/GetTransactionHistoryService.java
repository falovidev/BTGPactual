package com.btg.fondos.domain.transaction.service;

import com.btg.fondos.domain.client.exception.ClientNotFoundException;
import com.btg.fondos.domain.client.port.ClientRepository;
import com.btg.fondos.domain.transaction.model.Transaction;
import com.btg.fondos.domain.transaction.port.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetTransactionHistoryService {

    private final ClientRepository clientRepository;
    private final TransactionRepository transactionRepository;

    public List<Transaction> execute(String clientId) {
        clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(clientId));
        return transactionRepository.findByClientId(clientId);
    }
}
