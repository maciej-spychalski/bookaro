package pl.sztukakodu.bookaro.orders.application.price;

import pl.sztukakodu.bookaro.orders.domain.Order;

import java.math.BigDecimal;

public interface DiscountStrategy {
    BigDecimal calculate(Order order);
}
