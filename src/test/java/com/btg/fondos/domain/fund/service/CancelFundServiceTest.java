package com.btg.fondos.domain.fund.service;

import com.btg.fondos.domain.client.exception.ClientNotFoundException;
import com.btg.fondos.domain.client.model.Client;
import com.btg.fondos.domain.client.port.ClientRepository;
import com.btg.fondos.domain.fund.exception.FundNotFoundException;
import com.btg.fondos.domain.fund.exception.SubscriptionNotFoundException;
import com.btg.fondos.domain.fund.model.Fund;
import com.btg.fondos.domain.fund.model.Subscription;
import com.btg.fondos.domain.fund.port.FundRepository;
import com.btg.fondos.domain.fund.port.SubscriptionRepository;
import com.btg.fondos.domain.transaction.model.Transaction;
import com.btg.fondos.domain.transaction.model.TransactionType;
import com.btg.fondos.domain.transaction.port.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static com.btg.fondos.domain.testbuilder.ClientBuilder.aClient;
import static com.btg.fondos.domain.testbuilder.FundBuilder.aFund;
import static com.btg.fondos.domain.testbuilder.SubscriptionBuilder.aSubscription;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Cancelación de suscripciones")
class CancelFundServiceTest {

    @Mock private FundRepository fundRepository;
    @Mock private ClientRepository clientRepository;
    @Mock private SubscriptionRepository subscriptionRepository;
    @Mock private TransactionRepository transactionRepository;

    @InjectMocks
    private CancelFundService cancelFundService;

    private Client defaultClient;
    private Fund fundRecaudadora;

    @BeforeEach
    void setUp() {
        defaultClient = aClient().withBalance("425000").build();
        fundRecaudadora = aFund().build();
    }

    @Test
    @DisplayName("Debe cancelar y retornar el monto al saldo")
    void shouldCancelAndReturnBalance() {
        Subscription sub = aSubscription().build();

        when(clientRepository.findById("client-1")).thenReturn(Optional.of(defaultClient));
        when(fundRepository.findById("1")).thenReturn(Optional.of(fundRecaudadora));
        when(subscriptionRepository.findByClientIdAndFundId("client-1", "1")).thenReturn(Optional.of(sub));
        when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Transaction result = cancelFundService.cancel("client-1", "1");

        assertThat(result.getType()).isEqualTo(TransactionType.CANCELLATION);
        assertThat(defaultClient.getBalance()).isEqualByComparingTo(new BigDecimal("500000"));

        verify(subscriptionRepository).delete("client-1", "1");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando no hay suscripción activa")
    void shouldThrowWhenNoSubscription() {
        when(clientRepository.findById("client-1")).thenReturn(Optional.of(defaultClient));
        when(fundRepository.findById("1")).thenReturn(Optional.of(fundRecaudadora));
        when(subscriptionRepository.findByClientIdAndFundId("client-1", "1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cancelFundService.cancel("client-1", "1"))
                .isInstanceOf(SubscriptionNotFoundException.class);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el cliente no existe")
    void shouldThrowWhenClientNotFound() {
        when(clientRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cancelFundService.cancel("unknown", "1"))
                .isInstanceOf(ClientNotFoundException.class);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el fondo no existe")
    void shouldThrowWhenFundNotFound() {
        when(clientRepository.findById("client-1")).thenReturn(Optional.of(defaultClient));
        when(fundRepository.findById("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cancelFundService.cancel("client-1", "999"))
                .isInstanceOf(FundNotFoundException.class);
    }
}
