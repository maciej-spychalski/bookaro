package pl.sztukakodu.bookaro.orders.application.price;

import pl.sztukakodu.bookaro.orders.domain.Order;

import java.math.BigDecimal;

class DeliveryDiscountStrategy implements DiscountStrategy {

    public static final BigDecimal THRESHOLD = BigDecimal.valueOf(100);

    @Override
    public BigDecimal calculate(Order order) {
        if(order.getItemsPrice().compareTo(THRESHOLD) >= 0) {
            return order.getDeliveryPrice();
        }
        return BigDecimal.ZERO;
    }
}
