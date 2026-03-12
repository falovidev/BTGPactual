package com.btg.fondos.domain.common.port;

public interface UnitOfWork {

    UnitOfWork save(Object domainEntity);

    UnitOfWork delete(Class<?> entityType, Object... keys);

    void commit();
}
