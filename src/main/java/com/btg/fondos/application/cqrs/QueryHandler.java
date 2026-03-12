package com.btg.fondos.application.cqrs;

public interface QueryHandler<Q extends Query, R> {
    R handle(Q query);
}
