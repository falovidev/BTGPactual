package com.btg.fondos.domain.client.port;

import com.btg.fondos.domain.client.model.Client;

import java.util.Optional;

public interface ClientRepository {
    Client save(Client client);
    Optional<Client> findById(String clientId);
    Optional<Client> findByEmail(String email);
    boolean existsByEmail(String email);
}
