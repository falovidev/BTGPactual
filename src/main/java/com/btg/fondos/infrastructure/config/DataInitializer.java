package com.btg.fondos.infrastructure.config;

import com.btg.fondos.infrastructure.adapter.out.persistence.entity.ClientEntity;
import com.btg.fondos.infrastructure.adapter.out.persistence.entity.FundEntity;
import com.btg.fondos.infrastructure.adapter.out.persistence.entity.SubscriptionEntity;
import com.btg.fondos.infrastructure.adapter.out.persistence.entity.TransactionEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final DynamoDbClient dynamoDbClient;
    private final DynamoDbTable<ClientEntity> clientTable;
    private final DynamoDbTable<FundEntity> fundTable;
    private final DynamoDbTable<TransactionEntity> transactionTable;
    private final DynamoDbTable<SubscriptionEntity> subscriptionTable;

    @Override
    public void run(String... args) {
        createTableIfNotExists("Clients", "clientId", null,
                List.of(new GsiDef("email-index", "email")));
        createTableIfNotExists("Funds", "fundId", null, List.of());
        createTableIfNotExists("Transactions", "transactionId", null,
                List.of(new GsiDef("clientId-index", "clientId")));
        createTableIfNotExists("Subscriptions", "clientId", "fundId", List.of());

        seedFunds();
    }

    private void seedFunds() {
        var funds = List.of(
                createFund("1", "FPV_BTG_PACTUAL_RECAUDADORA", new BigDecimal("75000"), "FPV"),
                createFund("2", "FPV_BTG_PACTUAL_ECOPETROL", new BigDecimal("125000"), "FPV"),
                createFund("3", "DEUDAPRIVADA", new BigDecimal("50000"), "FIC"),
                createFund("4", "FDO-ACCIONES", new BigDecimal("250000"), "FIC"),
                createFund("5", "FPV_BTG_PACTUAL_DINAMICA", new BigDecimal("100000"), "FPV")
        );

        funds.forEach(fund -> {
            fundTable.putItem(fund);
            log.info("Fondo inicializado: {} - Monto mínimo: {}", fund.getName(), fund.getMinimumAmount());
        });
    }

    private FundEntity createFund(String id, String name, BigDecimal min, String category) {
        FundEntity fund = new FundEntity();
        fund.setFundId(id);
        fund.setName(name);
        fund.setMinimumAmount(min);
        fund.setCategory(category);
        return fund;
    }

    private record GsiDef(String indexName, String partitionKey) {}

    private void createTableIfNotExists(String tableName, String pk, String sk, List<GsiDef> gsis) {
        try {
            dynamoDbClient.describeTable(DescribeTableRequest.builder().tableName(tableName).build());
            log.info("Tabla {} ya existe", tableName);
        } catch (ResourceNotFoundException e) {
            var keySchema = new java.util.ArrayList<>(List.of(
                    KeySchemaElement.builder().attributeName(pk).keyType(KeyType.HASH).build()));

            var attrDefs = new java.util.ArrayList<>(List.of(
                    AttributeDefinition.builder().attributeName(pk).attributeType(ScalarAttributeType.S).build()));

            if (sk != null) {
                keySchema.add(KeySchemaElement.builder().attributeName(sk).keyType(KeyType.RANGE).build());
                attrDefs.add(AttributeDefinition.builder().attributeName(sk).attributeType(ScalarAttributeType.S).build());
            }

            var requestBuilder = CreateTableRequest.builder()
                    .tableName(tableName)
                    .keySchema(keySchema)
                    .attributeDefinitions(attrDefs)
                    .billingMode(BillingMode.PAY_PER_REQUEST);

            if (!gsis.isEmpty()) {
                var gsiList = gsis.stream().map(gsi -> {
                    attrDefs.add(AttributeDefinition.builder()
                            .attributeName(gsi.partitionKey())
                            .attributeType(ScalarAttributeType.S).build());
                    return GlobalSecondaryIndex.builder()
                            .indexName(gsi.indexName())
                            .keySchema(KeySchemaElement.builder()
                                    .attributeName(gsi.partitionKey())
                                    .keyType(KeyType.HASH).build())
                            .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
                            .build();
                }).toList();
                requestBuilder.globalSecondaryIndexes(gsiList);
                requestBuilder.attributeDefinitions(attrDefs);
            }

            dynamoDbClient.createTable(requestBuilder.build());
            log.info("Tabla {} creada exitosamente", tableName);
        }
    }
}
