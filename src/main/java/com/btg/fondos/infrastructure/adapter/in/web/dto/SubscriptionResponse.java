package com.btg.fondos.infrastructure.adapter.in.web.dto;

import com.btg.fondos.domain.fund.model.Subscription;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class SubscriptionResponse {
    private String fundId;
    private String fundName;
    private BigDecimal amount;
    private Instant subscribedAt;

    public static SubscriptionResponse from(Subscription s) {
        return SubscriptionResponse.builder()
                .fundId(s.getFundId())
                .fundName(s.getFundName())
                .amount(s.getAmount())
                .subscribedAt(s.getSubscribedAt())
                .build();
    }
}
