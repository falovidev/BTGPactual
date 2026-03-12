package com.btg.fondos.domain.transaction.port;

import com.btg.fondos.domain.transaction.model.Transaction;

import java.util.List;

public interface TransactionRepository {
    Transaction save(Transaction transaction);
    List<Transaction> findByClientId(String clientId);
}
