package com.btg.fondos.application.handler;

import com.btg.fondos.application.cqrs.QueryHandler;
import com.btg.fondos.application.query.GetClientByIdQuery;
import com.btg.fondos.domain.model.Client;
import com.btg.fondos.domain.port.out.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetClientByIdHandler implements QueryHandler<GetClientByIdQuery, Optional<Client>> {

    private final ClientRepository clientRepository;

    @Override
    public Optional<Client> handle(GetClientByIdQuery query) {
        return clientRepository.findById(query.clientId());
    }
}
