package com.btg.fondos.infrastructure.adapter.in.web;

import com.btg.fondos.domain.port.in.FundUseCase;
import com.btg.fondos.infrastructure.adapter.in.web.dto.TransactionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transacciones", description = "Historial de transacciones del cliente")
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {

    private final FundUseCase fundService;

    @GetMapping
    @Operation(summary = "Ver historial de transacciones",
            description = "Obtiene todas las transacciones (aperturas y cancelaciones) del cliente autenticado")
    public ResponseEntity<List<TransactionResponse>> getTransactions(Authentication authentication) {
        String clientId = authentication.getName();
        var transactions = fundService.getTransactionHistory(clientId).stream()
                .map(TransactionResponse::from)
                .toList();
        return ResponseEntity.ok(transactions);
    }
}
