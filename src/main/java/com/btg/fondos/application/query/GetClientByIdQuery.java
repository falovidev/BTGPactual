package com.btg.fondos.application.query;

import com.btg.fondos.application.cqrs.Query;

public record GetClientByIdQuery(String clientId) implements Query {
}
