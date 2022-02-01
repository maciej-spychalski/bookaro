package pl.sztukakodu.bookaro.order.application;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import pl.sztukakodu.bookaro.catalog.application.port.CatalogUseCase;
import pl.sztukakodu.bookaro.catalog.db.BookJpaRepository;
import pl.sztukakodu.bookaro.catalog.domain.Book;
import pl.sztukakodu.bookaro.order.application.port.QueryOrderUseCase;
import pl.sztukakodu.bookaro.order.domain.OrderStatus;
import pl.sztukakodu.bookaro.order.domain.Recipient;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static pl.sztukakodu.bookaro.order.application.port.ManipulateOrderUseCase.*;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class OrderServiceTest {

    @Autowired
    BookJpaRepository bookJpaRepository;

    @Autowired
    ManipulateOrderService service;

    @Autowired
    QueryOrderUseCase queryOrderService;

    @Autowired
    CatalogUseCase catalogUseCase;

    @Test
    public void userCanPlaceOrder() {
        // Given
        Book effectiveJava = givenEffectiveJava(50L);
        Book jcip = givenJavaConcurrency(50L);
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient())
                .item(new OrderItemCommand(effectiveJava.getId(), 15))
                .item(new OrderItemCommand(jcip.getId(), 10))
                .build();

        // When
        PlaceOrderResponse response = service.placeOrder(command);

        // Then
        assertTrue(response.isSuccess());
        assertEquals(35L, availableCopiesOf(effectiveJava));
        assertEquals(40L, availableCopiesOf(jcip));
    }

    @Test
    public void UserCanRevokeOrder() {
        // Given
        Book effectiveJava = givenEffectiveJava(50L);
        Long orderId = placeOrder(effectiveJava.getId(), 15);
        assertEquals(35L, availableCopiesOf(effectiveJava));

        // When
        service.updateOrderStatus(orderId, OrderStatus.CANCELED);

        // Then
        assertEquals(50L, availableCopiesOf(effectiveJava));
        assertEquals(OrderStatus.CANCELED, queryOrderService.findById(orderId).get().getStatus());

    }

    @Test
    public void userCannotRevokePaidOrder() {
        // Given
        Book effectiveJava = givenEffectiveJava(50L);
        Long orderId = placeOrder(effectiveJava.getId(), 15);
        assertEquals(35L, availableCopiesOf(effectiveJava));
        service.updateOrderStatus(orderId, OrderStatus.PAID);

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.updateOrderStatus(orderId, OrderStatus.CANCELED);
        });

        // Then
        assertTrue(exception.getMessage().contains("Unable to mark " + OrderStatus.PAID + " order as " + OrderStatus.CANCELED));
    }

    @Test
    public void userCannotRevokeShippedOrder() {
        // Given
        Book effectiveJava = givenEffectiveJava(50L);
        Long orderId = placeOrder(effectiveJava.getId(), 15);
        assertEquals(35L, availableCopiesOf(effectiveJava));
        service.updateOrderStatus(orderId, OrderStatus.PAID);
        service.updateOrderStatus(orderId, OrderStatus.SHIPPED);

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.updateOrderStatus(orderId, OrderStatus.CANCELED);
        });

        // Then
        assertTrue(exception.getMessage().contains("Unable to mark " + OrderStatus.SHIPPED + " order as " + OrderStatus.CANCELED));
    }

    @Test
    public void userCannotOrderNoExistingBooks() {
        Book effectiveJava = givenEffectiveJava(50L);
        Long bookId = effectiveJava.getId();
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient())
                .item(new OrderItemCommand(bookId + 1, 15))
                .build();

        //then
        assertThrows(EntityNotFoundException.class, () -> {
            service.placeOrder(command);
        });
    }

    @Test
    public void userCannotOrderNegativeNumberOfBooks() {
        Book effectiveJava = givenEffectiveJava(50L);
        Long bookId = effectiveJava.getId();
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient())
                .item(new OrderItemCommand(bookId, -15))
                .build();

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.placeOrder(command);;
        });

        // Then
        assertTrue(exception.getMessage().contains("The number of copies of the requested book" + bookId +
                " cannot be less than zero"));
    }

    private Long placeOrder(Long bookId, int copies) {
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient())
                .item(new OrderItemCommand(bookId, copies))
                .build();
        return service.placeOrder(command).getRight();
    }

    @Test
    public void userCantOrderMoreBooksThanAvailable() {
        // Given
        Book effectiveJava = givenEffectiveJava(5L);

        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient())
                .item(new OrderItemCommand(effectiveJava.getId(), 10))
                .build();

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.placeOrder(command);
        });

        // Then
        assertTrue(exception.getMessage().contains("Too many copies of book " + effectiveJava.getId() + " requested"));
    }

    private Book givenJavaConcurrency(long available) {
        return bookJpaRepository.save(new Book("Java Concurrency in Practice", 2006, new BigDecimal("99.90"), available));
    }

    private Book givenEffectiveJava(long available) {
        return bookJpaRepository.save(new Book("Effective Java", 2005, new BigDecimal("199.90"), available));
    }

    private Recipient recipient() {
        return Recipient.builder().email("john@example.org").build();
    }

    private Long availableCopiesOf(Book effectiveJava) {
        return catalogUseCase.findById(effectiveJava.getId())
                .get()
                .getAvailable();
    }

}