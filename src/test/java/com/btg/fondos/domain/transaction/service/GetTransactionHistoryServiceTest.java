package com.btg.fondos.domain.transaction.service;

import com.btg.fondos.domain.client.model.Client;
import com.btg.fondos.domain.client.port.ClientRepository;
import com.btg.fondos.domain.transaction.model.Transaction;
import com.btg.fondos.domain.transaction.model.TransactionType;
import com.btg.fondos.domain.transaction.port.TransactionRepository;
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
        Client client = new Client("client-1", "Juan Pérez", null, null,
                new BigDecimal("500000"), null, null, null);

        var transactions = List.of(
                new Transaction("tx-1", "client-1", null, null,
                        TransactionType.OPENING, null, null),
                new Transaction("tx-2", "client-1", null, null,
                        TransactionType.CANCELLATION, null, null)
        );
        when(clientRepository.findById("client-1")).thenReturn(Optional.of(client));
        when(transactionRepository.findByClientId("client-1")).thenReturn(transactions);

        List<Transaction> result = getTransactionHistoryService.execute("client-1");

        assertThat(result).hasSize(2);
    }
}
