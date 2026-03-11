package com.btg.fondos.infrastructure.adapter.out.persistence;

import com.btg.fondos.domain.model.Transaction;
import com.btg.fondos.domain.model.TransactionType;
import com.btg.fondos.domain.port.out.TransactionRepository;
import com.btg.fondos.infrastructure.adapter.out.persistence.entity.TransactionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DynamoDbTransactionRepository implements TransactionRepository {

    private final DynamoDbTable<TransactionEntity> transactionTable;

    @Override
    public Transaction save(Transaction transaction) {
        TransactionEntity entity = toEntity(transaction);
        transactionTable.putItem(entity);
        return transaction;
    }

    @Override
    public List<Transaction> findByClientId(String clientId) {
        DynamoDbIndex<TransactionEntity> index = transactionTable.index("clientId-index");
        var results = index.query(QueryConditional.keyEqualTo(
                Key.builder().partitionValue(clientId).build()));

        return results.stream()
                .flatMap(page -> page.items().stream())
                .map(this::toDomain)
                .toList();
    }

    private TransactionEntity toEntity(Transaction transaction) {
        TransactionEntity entity = new TransactionEntity();
        entity.setTransactionId(transaction.getTransactionId());
        entity.setClientId(transaction.getClientId());
        entity.setFundId(transaction.getFundId());
        entity.setFundName(transaction.getFundName());
        entity.setType(transaction.getType().name());
        entity.setAmount(transaction.getAmount());
        entity.setTimestamp(transaction.getTimestamp().toString());
        return entity;
    }

    private Transaction toDomain(TransactionEntity entity) {
        return Transaction.builder()
                .transactionId(entity.getTransactionId())
                .clientId(entity.getClientId())
                .fundId(entity.getFundId())
                .fundName(entity.getFundName())
                .type(TransactionType.valueOf(entity.getType()))
                .amount(new BigDecimal(entity.getAmount().toString()))
                .timestamp(Instant.parse(entity.getTimestamp()))
                .build();
    }
}
