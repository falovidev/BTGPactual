package com.btg.fondos.domain.transaction.service;

import com.btg.fondos.domain.client.exception.ClientNotFoundException;
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

import java.util.List;
import java.util.Optional;

import static com.btg.fondos.domain.testbuilder.ClientBuilder.aClient;
import static com.btg.fondos.domain.testbuilder.TransactionBuilder.aTransaction;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        Client client = aClient().build();

        var transactions = List.of(
                aTransaction().withTransactionId("tx-1").withType(TransactionType.OPENING).build(),
                aTransaction().withTransactionId("tx-2").withType(TransactionType.CANCELLATION).build()
        );
        when(clientRepository.findById("client-1")).thenReturn(Optional.of(client));
        when(transactionRepository.findByClientId("client-1")).thenReturn(transactions);

        List<Transaction> result = getTransactionHistoryService.execute("client-1");

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el cliente no existe")
    void shouldThrowWhenClientNotFound() {
        when(clientRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> getTransactionHistoryService.execute("unknown"))
                .isInstanceOf(ClientNotFoundException.class);
    }
}
