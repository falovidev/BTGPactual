package com.btg.fondos.application.client.query;

import com.btg.fondos.application.cqrs.Query;

public record GetClientByIdQuery(String clientId) implements Query {
}
