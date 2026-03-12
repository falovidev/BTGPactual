package com.btg.fondos.infrastructure.adapter.out.persistence;

import com.btg.fondos.domain.transaction.model.Transaction;
import com.btg.fondos.domain.transaction.model.TransactionType;
import com.btg.fondos.domain.transaction.port.TransactionRepository;
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
        return TransactionEntity.fromDomain(transaction);
    }

    private Transaction toDomain(TransactionEntity entity) {
        return new Transaction(
                entity.getTransactionId(), entity.getClientId(),
                entity.getFundId(), entity.getFundName(),
                TransactionType.valueOf(entity.getType()),
                new BigDecimal(entity.getAmount().toString()),
                Instant.parse(entity.getTimestamp()));
    }
}
