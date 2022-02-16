package pl.sztukakodu.bookaro.orders.application;

import lombok.Value;
import pl.sztukakodu.bookaro.orders.application.price.OrderPrice;
import pl.sztukakodu.bookaro.orders.domain.OrderItem;
import pl.sztukakodu.bookaro.orders.domain.OrderStatus;
import pl.sztukakodu.bookaro.orders.domain.Recipient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Value
public class RichOrder {
    Long id;
    OrderStatus status;
    Set<OrderItem> items;
    Recipient recipient;
    LocalDateTime createdAt;
    OrderPrice orderPrice;
    BigDecimal finalPrice;
}
