package com.btg.fondos.application.transaction.query;

import com.btg.fondos.application.cqrs.Query;

public record GetTransactionHistoryQuery(String clientId) implements Query {
}
