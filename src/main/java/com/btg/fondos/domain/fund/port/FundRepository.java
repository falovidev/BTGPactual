package com.btg.fondos.domain.fund.port;

import com.btg.fondos.domain.fund.model.Fund;

import java.util.List;
import java.util.Optional;

public interface FundRepository {
    Optional<Fund> findById(String fundId);
    List<Fund> findAll();
    Fund save(Fund fund);
}
