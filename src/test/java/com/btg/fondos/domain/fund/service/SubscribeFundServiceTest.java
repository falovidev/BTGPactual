package com.btg.fondos.domain.fund.service;

import com.btg.fondos.domain.client.exception.ClientNotFoundException;
import com.btg.fondos.domain.client.exception.InsufficientBalanceException;
import com.btg.fondos.domain.client.model.Client;
import com.btg.fondos.domain.client.port.ClientRepository;
import com.btg.fondos.domain.fund.exception.DuplicateSubscriptionException;
import com.btg.fondos.domain.fund.exception.FundNotFoundException;
import com.btg.fondos.domain.fund.model.Fund;
import com.btg.fondos.domain.fund.model.Subscription;
import com.btg.fondos.domain.fund.port.FundRepository;
import com.btg.fondos.domain.fund.port.SubscriptionRepository;
import com.btg.fondos.domain.notification.port.NotificationPort;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Suscripción a fondos")
class SubscribeFundServiceTest {

    @Mock private FundRepository fundRepository;
    @Mock private ClientRepository clientRepository;
    @Mock private SubscriptionRepository subscriptionRepository;
    @Mock private TransactionRepository transactionRepository;
    @Mock private NotificationPort notificationPort;

    @InjectMocks
    private SubscribeFundService subscribeFundService;

    private Client defaultClient;
    private Fund fundRecaudadora;
    private Fund fundAcciones;

    @BeforeEach
    void setUp() {
        defaultClient = aClient().build();

        fundRecaudadora = aFund().build();

        fundAcciones = aFund()
                .withFundId("4").withName("FDO-ACCIONES")
                .withMinimumAmount("250000").withCategory("FIC")
                .build();
    }

    @Test
    @DisplayName("Debe suscribirse exitosamente cuando hay saldo suficiente")
    void shouldSubscribeSuccessfully() {
        when(clientRepository.findById("client-1")).thenReturn(Optional.of(defaultClient));
        when(fundRepository.findById("1")).thenReturn(Optional.of(fundRecaudadora));
        when(subscriptionRepository.findByClientIdAndFundId("client-1", "1")).thenReturn(Optional.empty());
        when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Transaction result = subscribeFundService.subscribe("client-1", "1");

        assertThat(result.getType()).isEqualTo(TransactionType.OPENING);
        assertThat(result.getFundName()).isEqualTo("FPV_BTG_PACTUAL_RECAUDADORA");
        assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("75000"));
        assertThat(defaultClient.getBalance()).isEqualByComparingTo(new BigDecimal("425000"));

        verify(clientRepository).save(defaultClient);
        verify(subscriptionRepository).save(any(Subscription.class));
        verify(transactionRepository).save(any(Transaction.class));
        verify(notificationPort).sendNotification(eq(defaultClient), anyString(), anyString());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el saldo es insuficiente")
    void shouldThrowWhenInsufficientBalance() {
        Client poorClient = aClient().withBalance("50000").build();
        when(clientRepository.findById("client-1")).thenReturn(Optional.of(poorClient));
        when(fundRepository.findById("1")).thenReturn(Optional.of(fundRecaudadora));
        when(subscriptionRepository.findByClientIdAndFundId("client-1", "1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subscribeFundService.subscribe("client-1", "1"))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessageContaining("No tiene saldo disponible para vincularse al fondo FPV_BTG_PACTUAL_RECAUDADORA");

        verify(clientRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el fondo no existe")
    void shouldThrowWhenFundNotFound() {
        when(clientRepository.findById("client-1")).thenReturn(Optional.of(defaultClient));
        when(fundRepository.findById("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subscribeFundService.subscribe("client-1", "999"))
                .isInstanceOf(FundNotFoundException.class);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando ya está suscrito al fondo")
    void shouldThrowWhenAlreadySubscribed() {
        Subscription existing = aSubscription().build();
        when(clientRepository.findById("client-1")).thenReturn(Optional.of(defaultClient));
        when(fundRepository.findById("1")).thenReturn(Optional.of(fundRecaudadora));
        when(subscriptionRepository.findByClientIdAndFundId("client-1", "1"))
                .thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> subscribeFundService.subscribe("client-1", "1"))
                .isInstanceOf(DuplicateSubscriptionException.class);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el cliente no existe")
    void shouldThrowWhenClientNotFound() {
        when(clientRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subscribeFundService.subscribe("unknown", "1"))
                .isInstanceOf(ClientNotFoundException.class);
    }

    @Test
    @DisplayName("Debe permitir múltiples suscripciones si hay saldo")
    void shouldAllowMultipleSubscriptions() {
        when(clientRepository.findById("client-1")).thenReturn(Optional.of(defaultClient));
        when(fundRepository.findById("1")).thenReturn(Optional.of(fundRecaudadora));
        when(subscriptionRepository.findByClientIdAndFundId("client-1", "1")).thenReturn(Optional.empty());
        when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        subscribeFundService.subscribe("client-1", "1");
        assertThat(defaultClient.getBalance()).isEqualByComparingTo(new BigDecimal("425000"));

        when(fundRepository.findById("4")).thenReturn(Optional.of(fundAcciones));
        when(subscriptionRepository.findByClientIdAndFundId("client-1", "4")).thenReturn(Optional.empty());

        subscribeFundService.subscribe("client-1", "4");
        assertThat(defaultClient.getBalance()).isEqualByComparingTo(new BigDecimal("175000"));
    }
}
