package com.btg.fondos.infrastructure.adapter.out.persistence.uow;

import com.btg.fondos.domain.transaction.model.Transaction;
import com.btg.fondos.infrastructure.adapter.out.persistence.entity.TransactionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.TransactWriteItemsEnhancedRequest;

@Component
@RequiredArgsConstructor
public class TransactionEntityWriter implements EntityWriter<Transaction> {

    private final DynamoDbTable<TransactionEntity> transactionTable;

    @Override
    public Class<Transaction> supportedType() {
        return Transaction.class;
    }

    @Override
    public void addPut(TransactWriteItemsEnhancedRequest.Builder builder, Transaction transaction) {
        builder.addPutItem(transactionTable, TransactionEntity.fromDomain(transaction));
    }

    @Override
    public void addDelete(TransactWriteItemsEnhancedRequest.Builder builder, Object... keys) {
        builder.addDeleteItem(transactionTable,
                Key.builder().partitionValue((String) keys[0]).build());
    }
}
