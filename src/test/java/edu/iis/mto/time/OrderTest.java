package edu.iis.mto.time;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Or;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@ExtendWith(MockitoExtension.class)
class OrderTest {

    Instant submitInstant;
    Instant confirmedAfterExpiredInstant;
    Order order;

    @Mock
    private Clock mockClock;

    @BeforeEach
    void setUp() throws Exception {
        submitInstant = Instant.parse("2007-12-03T10:15:30.00Z");
        order = new Order(mockClock);
    }

    @Test
    void expiredOrderExceptionTest() {
        confirmedAfterExpiredInstant = submitInstant.plus(Order.VALID_PERIOD_HOURS + 1, ChronoUnit.HOURS);

        when(mockClock.getZone()).thenReturn(ZoneId.systemDefault());
        when(mockClock.instant()).thenReturn(submitInstant).thenReturn(confirmedAfterExpiredInstant);

        order.submit();

        try {
            order.confirm();
            fail("Confirmation after validation time, OrderExpiredException was not send");
        } catch (OrderExpiredException e) {}
    }

    @Test
    void expiredOrderStatusTest() {
        confirmedAfterExpiredInstant = submitInstant.plus(Order.VALID_PERIOD_HOURS + 1, ChronoUnit.HOURS);

        when(mockClock.getZone()).thenReturn(ZoneId.systemDefault());
        when(mockClock.instant()).thenReturn(submitInstant).thenReturn(confirmedAfterExpiredInstant);

        order.submit();

        try {
            order.confirm();
        } catch (OrderExpiredException e) {
            assertEquals(order.getOrderState(), Order.State.CANCELLED);
        }
    }

    @Test
    void notExpiredOrderExceptionTest() {
        confirmedAfterExpiredInstant = submitInstant.plus(Order.VALID_PERIOD_HOURS - 5, ChronoUnit.HOURS);

        when(mockClock.getZone()).thenReturn(ZoneId.systemDefault());
        when(mockClock.instant()).thenReturn(submitInstant).thenReturn(confirmedAfterExpiredInstant);

        order.submit();

        try {
            order.confirm();
        } catch (OrderExpiredException e) {
            fail("OrderExpiredException was thrown, when order still valid");
        }
    }

    @Test
    void notExpiredOrderStatusTest() {confirmedAfterExpiredInstant = submitInstant.plus(Order.VALID_PERIOD_HOURS - 5, ChronoUnit.HOURS);

        when(mockClock.getZone()).thenReturn(ZoneId.systemDefault());
        when(mockClock.instant()).thenReturn(submitInstant).thenReturn(confirmedAfterExpiredInstant);

        order.submit();

        try {
            order.confirm();
        } catch (OrderExpiredException e) {
            assertEquals(order.getOrderState(), Order.State.CONFIRMED);
        }}
}
