package pl.sztukakodu.bookaro.order.application.price;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.sztukakodu.bookaro.order.domain.Order;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PriceService {
    private final List<DiscountStrategy> srategies = List.of(
            new DeliveryDiscountStrategy(),
            new TotalPriceDiscountStrategy()
    );

    @Transactional
    public OrderPrice calculatePrice(Order order) {
        return new OrderPrice(
                order.getItemsPrice(),
                order.getDeliveryPrice(),
                discounts(order)
        );
    }

    private BigDecimal discounts(Order order) {
        return srategies
                .stream()
                .map(stragegy -> stragegy.calculate(order))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
