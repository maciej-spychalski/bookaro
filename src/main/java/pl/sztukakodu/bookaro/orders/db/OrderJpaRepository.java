package pl.sztukakodu.bookaro.orders.db;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.sztukakodu.bookaro.orders.domain.Order;
import pl.sztukakodu.bookaro.orders.domain.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatusAndCreatedAtLessThanEqual(OrderStatus status, LocalDateTime timestamp);
}
