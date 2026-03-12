package com.btg.fondos.domain.service;

import com.btg.fondos.domain.model.Client;
import com.btg.fondos.domain.port.out.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    public Optional<Client> getClientById(String clientId) {
        return clientRepository.findById(clientId);
    }
}
