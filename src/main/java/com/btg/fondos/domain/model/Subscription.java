package com.btg.fondos.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {
    private String clientId;
    private String fundId;
    private String fundName;
    private BigDecimal amount;
    private Instant subscribedAt;
}
