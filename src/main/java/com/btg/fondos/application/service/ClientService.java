package com.btg.fondos.application.service;

import com.btg.fondos.domain.model.Client;
import com.btg.fondos.domain.port.in.ClientUseCase;
import com.btg.fondos.domain.port.out.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientService implements ClientUseCase {

    private final ClientRepository clientRepository;

    @Override
    public Optional<Client> getClientById(String clientId) {
        return clientRepository.findById(clientId);
    }
}
