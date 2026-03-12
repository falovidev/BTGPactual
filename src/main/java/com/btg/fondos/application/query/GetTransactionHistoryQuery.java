package com.btg.fondos.application.query;

import com.btg.fondos.application.cqrs.Query;

public record GetTransactionHistoryQuery(String clientId) implements Query {
}
