package com.btg.fondos.infrastructure.adapter.in.web;

import com.btg.fondos.domain.port.in.FundUseCase;
import com.btg.fondos.infrastructure.adapter.in.web.dto.FundResponse;
import com.btg.fondos.infrastructure.adapter.in.web.dto.SubscriptionResponse;
import com.btg.fondos.infrastructure.adapter.in.web.dto.TransactionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/funds")
@RequiredArgsConstructor
@Tag(name = "Fondos", description = "Gestión de fondos de inversión")
@SecurityRequirement(name = "bearerAuth")
public class FundController {

    private final FundUseCase fundService;

    @GetMapping
    @Operation(summary = "Listar fondos", description = "Obtiene todos los fondos de inversión disponibles")
    public ResponseEntity<List<FundResponse>> getAllFunds() {
        var funds = fundService.getAllFunds().stream()
                .map(FundResponse::from)
                .toList();
        return ResponseEntity.ok(funds);
    }

    @PostMapping("/{fundId}/subscribe")
    @Operation(summary = "Suscribirse a un fondo", description = "Vincula al cliente autenticado con el fondo especificado")
    public ResponseEntity<TransactionResponse> subscribe(@PathVariable String fundId,
                                                          Authentication authentication) {
        String clientId = authentication.getName();
        var transaction = fundService.subscribe(clientId, fundId);
        return ResponseEntity.status(HttpStatus.CREATED).body(TransactionResponse.from(transaction));
    }

    @DeleteMapping("/{fundId}/cancel")
    @Operation(summary = "Cancelar suscripción", description = "Cancela la vinculación al fondo y retorna el monto al saldo")
    public ResponseEntity<TransactionResponse> cancel(@PathVariable String fundId,
                                                       Authentication authentication) {
        String clientId = authentication.getName();
        var transaction = fundService.cancel(clientId, fundId);
        return ResponseEntity.ok(TransactionResponse.from(transaction));
    }

    @GetMapping("/subscriptions")
    @Operation(summary = "Ver suscripciones activas", description = "Lista los fondos a los que está suscrito el cliente")
    public ResponseEntity<List<SubscriptionResponse>> getSubscriptions(Authentication authentication) {
        String clientId = authentication.getName();
        var subs = fundService.getClientSubscriptions(clientId).stream()
                .map(SubscriptionResponse::from)
                .toList();
        return ResponseEntity.ok(subs);
    }
}
