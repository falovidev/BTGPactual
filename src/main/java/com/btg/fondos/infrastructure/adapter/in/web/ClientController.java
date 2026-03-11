package com.btg.fondos.infrastructure.adapter.in.web;

import com.btg.fondos.domain.exception.ClientNotFoundException;
import com.btg.fondos.domain.model.Client;
import com.btg.fondos.domain.port.in.ClientUseCase;
import com.btg.fondos.infrastructure.adapter.in.web.dto.ClientResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Información del cliente autenticado")
@SecurityRequirement(name = "bearerAuth")
public class ClientController {

    private final ClientUseCase clientService;

    @GetMapping("/me")
    @Operation(summary = "Ver perfil", description = "Obtiene el perfil y saldo del cliente autenticado")
    public ResponseEntity<ClientResponse> getProfile(Authentication authentication) {
        String clientId = authentication.getName();
        Client client = clientService.getClientById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(clientId));
        return ResponseEntity.ok(ClientResponse.from(client));
    }
}
