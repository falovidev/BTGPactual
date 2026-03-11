package com.btg.fondos.infrastructure.adapter.in.web.dto;

import com.btg.fondos.domain.model.Transaction;
import com.btg.fondos.domain.model.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class TransactionResponse {
    private String transactionId;
    private String fundId;
    private String fundName;
    private TransactionType type;
    private BigDecimal amount;
    private Instant timestamp;

    public static TransactionResponse from(Transaction t) {
        return TransactionResponse.builder()
                .transactionId(t.getTransactionId())
                .fundId(t.getFundId())
                .fundName(t.getFundName())
                .type(t.getType())
                .amount(t.getAmount())
                .timestamp(t.getTimestamp())
                .build();
    }
}
