package pl.sztukakodu.bookaro.orders.application.price;

import org.junit.jupiter.api.Test;
import pl.sztukakodu.bookaro.catalog.domain.Book;
import pl.sztukakodu.bookaro.orders.domain.Order;
import pl.sztukakodu.bookaro.orders.domain.OrderItem;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PriceServiceTest {

    PriceService priceService = new PriceService();

    @Test
    public void calculatesTotalPriceOfEmptyOrder() {
        // Given
        Order order = Order
                .builder()
                .build();

        // When
        OrderPrice price = priceService.calculatePrice(order);

        // Then
        assertEquals(BigDecimal.ZERO, price.finalPrice());
    }

    @Test
    public void calculatesTotalPrice() {
        // Given
        Book book1 = new Book();
        book1.setPrice(new BigDecimal("2.50"));
        Book book2 = new Book();
        book2.setPrice(new BigDecimal("3.49"));

        Order order = Order
                .builder()
                .item(new OrderItem(book1, 4))
                .item(new OrderItem(book2, 2))
                .build();

        // When
        OrderPrice price = priceService.calculatePrice(order);
        // Then
        assertEquals(new BigDecimal("26.88"), price.finalPrice());
        assertEquals(new BigDecimal("16.98"), price.getItemsPrice());
        assertEquals(new BigDecimal("9.90"), price.getDeliveryPrice());
    }

}