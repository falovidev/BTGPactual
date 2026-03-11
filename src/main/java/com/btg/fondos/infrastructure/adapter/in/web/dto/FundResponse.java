package com.btg.fondos.infrastructure.adapter.in.web.dto;

import com.btg.fondos.domain.model.Fund;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class FundResponse {
    private String fundId;
    private String name;
    private BigDecimal minimumAmount;
    private String category;

    public static FundResponse from(Fund f) {
        return FundResponse.builder()
                .fundId(f.getFundId())
                .name(f.getName())
                .minimumAmount(f.getMinimumAmount())
                .category(f.getCategory())
                .build();
    }
}
