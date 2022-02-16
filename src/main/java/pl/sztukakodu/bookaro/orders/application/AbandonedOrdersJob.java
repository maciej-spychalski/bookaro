package pl.sztukakodu.bookaro.orders.application;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.sztukakodu.bookaro.clock.Clock;
import pl.sztukakodu.bookaro.orders.application.port.ManipulateOrderUseCase;
import pl.sztukakodu.bookaro.orders.db.OrderJpaRepository;
import pl.sztukakodu.bookaro.orders.domain.Order;
import pl.sztukakodu.bookaro.orders.domain.OrderStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static pl.sztukakodu.bookaro.orders.application.port.ManipulateOrderUseCase.*;

@Slf4j
@Component
@AllArgsConstructor
public class AbandonedOrdersJob {
    private final OrderJpaRepository repository;
    private final ManipulateOrderUseCase orderUseCase;
    private final OrdersProperties properties;
    private User systemUser;
    private final Clock clock;

    @Scheduled(cron = "${app.orders.abandon-cron}")
    @Transactional
    public void run() {
        Duration paymentPeriod = properties.getPaymentPeriod();
        LocalDateTime olderThan = clock.now().minus(paymentPeriod);
        List<Order> orders = repository.findByStatusAndCreatedAtLessThanEqual(OrderStatus.NEW, olderThan);
        log.info("Found orders to be abandoned: " + orders.size());
        orders.forEach(order -> {
            UpdateStatusCommand command = new UpdateStatusCommand(order.getId(), OrderStatus.ABANDONED, systemUser);
            orderUseCase.updateOrderStatus(command);
        });
    }
}
