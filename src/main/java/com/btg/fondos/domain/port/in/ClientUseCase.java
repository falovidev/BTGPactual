package com.btg.fondos.domain.port.in;

import com.btg.fondos.domain.model.Client;

import java.util.Optional;

public interface ClientUseCase {
    Optional<Client> getClientById(String clientId);
}
