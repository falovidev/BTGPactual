package com.btg.fondos.infrastructure.adapter.out.persistence;

import com.btg.fondos.domain.client.model.Client;
import com.btg.fondos.domain.notification.model.NotificationType;
import com.btg.fondos.domain.client.model.Role;
import com.btg.fondos.domain.client.port.ClientRepository;
import com.btg.fondos.infrastructure.adapter.out.persistence.entity.ClientEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DynamoDbClientRepository implements ClientRepository {

    private final DynamoDbTable<ClientEntity> clientTable;

    @Override
    public Client save(Client client) {
        ClientEntity entity = toEntity(client);
        clientTable.putItem(entity);
        return client;
    }

    @Override
    public Optional<Client> findById(String clientId) {
        ClientEntity entity = clientTable.getItem(
                Key.builder().partitionValue(clientId).build());
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public Optional<Client> findByEmail(String email) {
        DynamoDbIndex<ClientEntity> emailIndex = clientTable.index("email-index");
        var results = emailIndex.query(QueryConditional.keyEqualTo(
                Key.builder().partitionValue(email).build()));

        return results.stream()
                .flatMap(page -> page.items().stream())
                .findFirst()
                .map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    private ClientEntity toEntity(Client client) {
        return ClientEntity.fromDomain(client);
    }

    private Client toDomain(ClientEntity entity) {
        return new Client(
                entity.getClientId(), entity.getName(), entity.getEmail(), entity.getPhone(),
                entity.getBalance(), NotificationType.valueOf(entity.getNotificationPreference()),
                entity.getPassword(), Role.valueOf(entity.getRole()));
    }
}
