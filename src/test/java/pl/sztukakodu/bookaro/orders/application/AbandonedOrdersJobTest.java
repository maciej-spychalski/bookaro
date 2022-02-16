package pl.sztukakodu.bookaro.orders.application;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import pl.sztukakodu.bookaro.catalog.application.port.CatalogUseCase;
import pl.sztukakodu.bookaro.catalog.db.BookJpaRepository;
import pl.sztukakodu.bookaro.catalog.domain.Book;
import pl.sztukakodu.bookaro.clock.Clock;
import pl.sztukakodu.bookaro.orders.application.port.ManipulateOrderUseCase;
import pl.sztukakodu.bookaro.orders.application.port.QueryOrderUseCase;
import pl.sztukakodu.bookaro.orders.domain.OrderStatus;
import pl.sztukakodu.bookaro.orders.domain.Recipient;

import java.math.BigDecimal;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(
        properties = "app.orders.payment-period=1H"
)
@AutoConfigureTestDatabase
class AbandonedOrdersJobTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public Clock.Fake clock() {
            return new Clock.Fake();
        }
    }

    @Autowired
    Clock.Fake clock;

    @Autowired
    BookJpaRepository bookJpaRepository;

    @Autowired
    QueryOrderUseCase queryOrderService;

    @Autowired
    CatalogUseCase catalogUseCase;

    @Autowired
    ManipulateOrderService manipulateOrderService;

    @Autowired
    AbandonedOrdersJob ordersJob;

    @Test
    public void shouldMarkOrdersAsAbandoned() {
        // Given
        Book book = givenEffectiveJava(50L);
        Long orderId = placeOrder(book.getId(), 15);

        // When
        clock.tick(Duration.ofHours(2));
        ordersJob.run();

        // Then
        assertEquals(OrderStatus.ABANDONED, queryOrderService.findById(orderId).get().getStatus());
        assertEquals(50L, availableCopiesOf(book));
    }

    private Long placeOrder(Long bookId, int copies) {
        ManipulateOrderUseCase.PlaceOrderCommand command = ManipulateOrderUseCase.PlaceOrderCommand
                .builder()
                .recipient(recipient())
                .item(new ManipulateOrderUseCase.OrderItemCommand(bookId, copies))
                .build();
        return manipulateOrderService.placeOrder(command).getRight();
    }

    private Recipient recipient() {
        return Recipient.builder().email("marek@example.org").build();
    }

    private Book givenEffectiveJava(long available) {
        return bookJpaRepository.save(new Book("Effective Java", 2005, new BigDecimal("199.90"), available));
    }

    private Long availableCopiesOf(Book book) {
        return catalogUseCase.findById(book.getId())
                .get()
                .getAvailable();
    }
}

