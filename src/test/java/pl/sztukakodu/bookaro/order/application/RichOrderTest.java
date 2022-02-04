package pl.sztukakodu.bookaro.order.application;

import org.junit.jupiter.api.Test;
import pl.sztukakodu.bookaro.catalog.domain.Book;
import pl.sztukakodu.bookaro.order.domain.OrderItem;
import pl.sztukakodu.bookaro.order.domain.OrderStatus;
import pl.sztukakodu.bookaro.order.domain.Recipient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RichOrderTest {


//    @Test
//    public void calculatesTotalPriceOfEmptyOrder() {
//        // Given
//        RichOrder order = new RichOrder(
//                1L,
//                OrderStatus.NEW,
//                Collections.emptySet(),
//                Recipient.builder().build(),
//                LocalDateTime.now()
//        );
//        // When
//        BigDecimal price = order.totalPrice();
//        // Then
//        assertEquals(BigDecimal.ZERO, price);
//    }
//
//    @Test
//    public void calculatesTotalPrice() {
//        // Given
//        Book book1 = new Book();
//        book1.setPrice(new BigDecimal("2.50"));
//        Book book2 = new Book();
//        book2.setPrice(new BigDecimal("3.49"));
//        Set<OrderItem> items = new HashSet<>(
//                Arrays.asList(
//                        new OrderItem(book1, 4),
//                        new OrderItem(book2, 2)
//                )
//        );
//        RichOrder order = new RichOrder(
//                1L,
//                OrderStatus.NEW,
//                items,
//                Recipient.builder().build(),
//                LocalDateTime.now()
//        );
//        // When
//        BigDecimal price = order.totalPrice();
//        // Then
//        assertEquals(new BigDecimal("16.98"), price);
//    }
}