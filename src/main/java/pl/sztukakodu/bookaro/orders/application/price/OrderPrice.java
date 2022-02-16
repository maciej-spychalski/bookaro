package pl.sztukakodu.bookaro.orders.application.price;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class OrderPrice {
    BigDecimal itemsPrice;
    BigDecimal deliveryPrice;
    BigDecimal discounts;

    public BigDecimal finalPrice() {
        return itemsPrice.add(deliveryPrice).subtract(discounts);
    }
}
