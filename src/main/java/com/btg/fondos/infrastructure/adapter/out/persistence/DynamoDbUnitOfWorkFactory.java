package com.btg.fondos.infrastructure.adapter.out.persistence;

import com.btg.fondos.domain.common.port.UnitOfWork;
import com.btg.fondos.domain.common.port.UnitOfWorkFactory;
import com.btg.fondos.infrastructure.adapter.out.persistence.uow.EntityWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DynamoDbUnitOfWorkFactory implements UnitOfWorkFactory {

    private final DynamoDbEnhancedClient enhancedClient;
    private final Map<Class<?>, EntityWriter<?>> writers;

    public DynamoDbUnitOfWorkFactory(DynamoDbEnhancedClient enhancedClient,
                                     List<EntityWriter<?>> writerList) {
        this.enhancedClient = enhancedClient;
        this.writers = writerList.stream()
                .collect(Collectors.toMap(EntityWriter::supportedType, w -> w));
    }

    @Override
    public UnitOfWork create() {
        return new DynamoDbUnitOfWork(enhancedClient, writers);
    }
}
