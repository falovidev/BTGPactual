package com.btg.fondos.infrastructure.config;

import com.btg.fondos.infrastructure.adapter.out.persistence.entity.ClientEntity;
import com.btg.fondos.infrastructure.adapter.out.persistence.entity.FundEntity;
import com.btg.fondos.infrastructure.adapter.out.persistence.entity.SubscriptionEntity;
import com.btg.fondos.infrastructure.adapter.out.persistence.entity.TransactionEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

@Configuration
public class DynamoDbConfig {

    @Value("${aws.dynamodb.endpoint:}")
    private String endpoint;

    @Value("${aws.dynamodb.region:us-east-1}")
    private String region;

    @Bean
    public DynamoDbClient dynamoDbClient() {
        if (endpoint != null && !endpoint.isBlank()) {
            return DynamoDbClient.builder()
                    .endpointOverride(URI.create(endpoint))
                    .region(Region.of(region))
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create("local", "local")))
                    .build();
        }
        return DynamoDbClient.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }

    @Bean
    public DynamoDbTable<ClientEntity> clientTable(DynamoDbEnhancedClient enhancedClient) {
        return enhancedClient.table("Clients", TableSchema.fromBean(ClientEntity.class));
    }

    @Bean
    public DynamoDbTable<FundEntity> fundTable(DynamoDbEnhancedClient enhancedClient) {
        return enhancedClient.table("Funds", TableSchema.fromBean(FundEntity.class));
    }

    @Bean
    public DynamoDbTable<TransactionEntity> transactionTable(DynamoDbEnhancedClient enhancedClient) {
        return enhancedClient.table("Transactions", TableSchema.fromBean(TransactionEntity.class));
    }

    @Bean
    public DynamoDbTable<SubscriptionEntity> subscriptionTable(DynamoDbEnhancedClient enhancedClient) {
        return enhancedClient.table("Subscriptions", TableSchema.fromBean(SubscriptionEntity.class));
    }
}
