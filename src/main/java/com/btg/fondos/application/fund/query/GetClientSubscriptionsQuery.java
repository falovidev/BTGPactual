package com.btg.fondos.application.fund.query;

import com.btg.fondos.application.cqrs.Query;

public record GetClientSubscriptionsQuery(String clientId) implements Query {
}
