package com.btg.fondos.infrastructure.adapter.out.persistence;

import com.btg.fondos.domain.model.Fund;
import com.btg.fondos.domain.port.out.FundRepository;
import com.btg.fondos.infrastructure.adapter.out.persistence.entity.FundEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DynamoDbFundRepository implements FundRepository {

    private final DynamoDbTable<FundEntity> fundTable;

    @Override
    public Optional<Fund> findById(String fundId) {
        FundEntity entity = fundTable.getItem(
                Key.builder().partitionValue(fundId).build());
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public List<Fund> findAll() {
        return fundTable.scan().items().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Fund save(Fund fund) {
        FundEntity entity = toEntity(fund);
        fundTable.putItem(entity);
        return fund;
    }

    private FundEntity toEntity(Fund fund) {
        FundEntity entity = new FundEntity();
        entity.setFundId(fund.getFundId());
        entity.setName(fund.getName());
        entity.setMinimumAmount(fund.getMinimumAmount());
        entity.setCategory(fund.getCategory());
        return entity;
    }

    private Fund toDomain(FundEntity entity) {
        return new Fund(
                entity.getFundId(), entity.getName(),
                entity.getMinimumAmount(), entity.getCategory());
    }
}
