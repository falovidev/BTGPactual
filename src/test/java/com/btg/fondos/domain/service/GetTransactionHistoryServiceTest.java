package com.btg.fondos.domain.service;

import com.btg.fondos.domain.model.*;
import com.btg.fondos.domain.port.out.ClientRepository;
import com.btg.fondos.domain.port.out.TransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Historial de transacciones")
class GetTransactionHistoryServiceTest {

    @Mock private ClientRepository clientRepository;
    @Mock private TransactionRepository transactionRepository;

    @InjectMocks
    private GetTransactionHistoryService getTransactionHistoryService;

    @Test
    @DisplayName("Debe retornar historial de transacciones del cliente")
    void shouldReturnTransactionHistory() {
        Client client = Client.builder()
                .clientId("client-1")
                .name("Juan Pérez")
                .balance(new BigDecimal("500000"))
                .build();

        var transactions = List.of(
                Transaction.builder().transactionId("tx-1").clientId("client-1")
                        .type(TransactionType.OPENING).build(),
                Transaction.builder().transactionId("tx-2").clientId("client-1")
                        .type(TransactionType.CANCELLATION).build()
        );
        when(clientRepository.findById("client-1")).thenReturn(Optional.of(client));
        when(transactionRepository.findByClientId("client-1")).thenReturn(transactions);

        List<Transaction> result = getTransactionHistoryService.execute("client-1");

        assertThat(result).hasSize(2);
    }
}
