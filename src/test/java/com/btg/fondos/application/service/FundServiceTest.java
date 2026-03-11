package com.btg.fondos.application.service;

import com.btg.fondos.domain.exception.DuplicateSubscriptionException;
import com.btg.fondos.domain.exception.FundNotFoundException;
import com.btg.fondos.domain.exception.InsufficientBalanceException;
import com.btg.fondos.domain.exception.SubscriptionNotFoundException;
import com.btg.fondos.domain.model.*;
import com.btg.fondos.domain.port.out.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FundServiceTest {

    @Mock private FundRepository fundRepository;
    @Mock private ClientRepository clientRepository;
    @Mock private SubscriptionRepository subscriptionRepository;
    @Mock private TransactionRepository transactionRepository;
    @Mock private NotificationPort notificationPort;

    @InjectMocks
    private FundService fundService;

    private Client defaultClient;
    private Fund fundRecaudadora;
    private Fund fundAcciones;

    @BeforeEach
    void setUp() {
        defaultClient = Client.builder()
                .clientId("client-1")
                .name("Juan Pérez")
                .email("juan@test.com")
                .phone("+573001234567")
                .balance(new BigDecimal("500000"))
                .notificationPreference(NotificationType.EMAIL)
                .role(Role.USER)
                .build();

        fundRecaudadora = Fund.builder()
                .fundId("1")
                .name("FPV_BTG_PACTUAL_RECAUDADORA")
                .minimumAmount(new BigDecimal("75000"))
                .category("FPV")
                .build();

        fundAcciones = Fund.builder()
                .fundId("4")
                .name("FDO-ACCIONES")
                .minimumAmount(new BigDecimal("250000"))
                .category("FIC")
                .build();
    }

    @Nested
    @DisplayName("Suscripción a fondos")
    class SubscribeTests {

        @Test
        @DisplayName("Debe suscribirse exitosamente cuando hay saldo suficiente")
        void shouldSubscribeSuccessfully() {
            when(clientRepository.findById("client-1")).thenReturn(Optional.of(defaultClient));
            when(fundRepository.findById("1")).thenReturn(Optional.of(fundRecaudadora));
            when(subscriptionRepository.findByClientIdAndFundId("client-1", "1")).thenReturn(Optional.empty());
            when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Transaction result = fundService.subscribe("client-1", "1");

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
            defaultClient.setBalance(new BigDecimal("50000"));
            when(clientRepository.findById("client-1")).thenReturn(Optional.of(defaultClient));
            when(fundRepository.findById("1")).thenReturn(Optional.of(fundRecaudadora));
            when(subscriptionRepository.findByClientIdAndFundId("client-1", "1")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> fundService.subscribe("client-1", "1"))
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

            assertThatThrownBy(() -> fundService.subscribe("client-1", "999"))
                    .isInstanceOf(FundNotFoundException.class);
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando ya está suscrito al fondo")
        void shouldThrowWhenAlreadySubscribed() {
            Subscription existing = Subscription.builder()
                    .clientId("client-1").fundId("1").build();
            when(clientRepository.findById("client-1")).thenReturn(Optional.of(defaultClient));
            when(fundRepository.findById("1")).thenReturn(Optional.of(fundRecaudadora));
            when(subscriptionRepository.findByClientIdAndFundId("client-1", "1"))
                    .thenReturn(Optional.of(existing));

            assertThatThrownBy(() -> fundService.subscribe("client-1", "1"))
                    .isInstanceOf(DuplicateSubscriptionException.class);
        }

        @Test
        @DisplayName("Debe permitir múltiples suscripciones si hay saldo")
        void shouldAllowMultipleSubscriptions() {
            when(clientRepository.findById("client-1")).thenReturn(Optional.of(defaultClient));
            when(fundRepository.findById("1")).thenReturn(Optional.of(fundRecaudadora));
            when(subscriptionRepository.findByClientIdAndFundId("client-1", "1")).thenReturn(Optional.empty());
            when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            fundService.subscribe("client-1", "1");
            assertThat(defaultClient.getBalance()).isEqualByComparingTo(new BigDecimal("425000"));

            when(fundRepository.findById("4")).thenReturn(Optional.of(fundAcciones));
            when(subscriptionRepository.findByClientIdAndFundId("client-1", "4")).thenReturn(Optional.empty());

            fundService.subscribe("client-1", "4");
            assertThat(defaultClient.getBalance()).isEqualByComparingTo(new BigDecimal("175000"));
        }
    }

    @Nested
    @DisplayName("Cancelación de suscripciones")
    class CancelTests {

        @Test
        @DisplayName("Debe cancelar y retornar el monto al saldo")
        void shouldCancelAndReturnBalance() {
            Subscription sub = Subscription.builder()
                    .clientId("client-1").fundId("1")
                    .fundName("FPV_BTG_PACTUAL_RECAUDADORA")
                    .amount(new BigDecimal("75000"))
                    .subscribedAt(Instant.now())
                    .build();

            defaultClient.setBalance(new BigDecimal("425000"));
            when(clientRepository.findById("client-1")).thenReturn(Optional.of(defaultClient));
            when(fundRepository.findById("1")).thenReturn(Optional.of(fundRecaudadora));
            when(subscriptionRepository.findByClientIdAndFundId("client-1", "1")).thenReturn(Optional.of(sub));
            when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Transaction result = fundService.cancel("client-1", "1");

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

            assertThatThrownBy(() -> fundService.cancel("client-1", "1"))
                    .isInstanceOf(SubscriptionNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Historial de transacciones")
    class TransactionHistoryTests {

        @Test
        @DisplayName("Debe retornar historial de transacciones del cliente")
        void shouldReturnTransactionHistory() {
            var transactions = List.of(
                    Transaction.builder().transactionId("tx-1").clientId("client-1")
                            .type(TransactionType.OPENING).build(),
                    Transaction.builder().transactionId("tx-2").clientId("client-1")
                            .type(TransactionType.CANCELLATION).build()
            );
            when(clientRepository.findById("client-1")).thenReturn(Optional.of(defaultClient));
            when(transactionRepository.findByClientId("client-1")).thenReturn(transactions);

            List<Transaction> result = fundService.getTransactionHistory("client-1");

            assertThat(result).hasSize(2);
        }
    }
}
