package com.btg.fondos.domain.service;

import com.btg.fondos.domain.exception.SubscriptionNotFoundException;
import com.btg.fondos.domain.model.*;
import com.btg.fondos.domain.port.out.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

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
        defaultClient = new Client("client-1", "Juan Pérez", "juan@test.com", "+573001234567",
                new BigDecimal("425000"), NotificationType.EMAIL, null, Role.USER);

        fundRecaudadora = new Fund("1", "FPV_BTG_PACTUAL_RECAUDADORA",
                new BigDecimal("75000"), "FPV");
    }

    @Test
    @DisplayName("Debe cancelar y retornar el monto al saldo")
    void shouldCancelAndReturnBalance() {
        Subscription sub = new Subscription("client-1", "1",
                "FPV_BTG_PACTUAL_RECAUDADORA", new BigDecimal("75000"), Instant.now());

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
}
