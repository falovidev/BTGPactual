package com.btg.fondos.infrastructure.adapter.in.web;

import com.btg.fondos.domain.model.Client;
import com.btg.fondos.domain.port.in.AuthUseCase;
import com.btg.fondos.infrastructure.adapter.in.web.dto.ClientResponse;
import com.btg.fondos.infrastructure.adapter.in.web.dto.LoginRequest;
import com.btg.fondos.infrastructure.adapter.in.web.dto.LoginResponse;
import com.btg.fondos.infrastructure.adapter.in.web.dto.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Registro y login de clientes")
public class AuthController {

    private final AuthUseCase authService;

    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo cliente", description = "Crea un nuevo cliente con saldo inicial de COP $500.000")
    public ResponseEntity<ClientResponse> register(@Valid @RequestBody RegisterRequest request) {
        Client client = authService.register(
                request.getName(),
                request.getEmail(),
                request.getPhone(),
                request.getPassword(),
                request.getNotificationPreference()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ClientResponse.from(client));
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Retorna un token JWT para autenticación")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthUseCase.AuthResult result = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(new LoginResponse(
                result.token(), result.client().getClientId(),
                result.client().getName(), result.client().getEmail()));
    }
}
