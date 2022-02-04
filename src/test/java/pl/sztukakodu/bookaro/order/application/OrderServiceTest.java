package pl.sztukakodu.bookaro.order.application;

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
        String recipient = "marek@example.org";
        Long orderId = placeOrder(effectiveJava.getId(), 15, recipient);
        assertEquals(35L, availableCopiesOf(effectiveJava));

        // When
        UpdateStatusCommand command = new UpdateStatusCommand(orderId, OrderStatus.CANCELED,recipient);
        service.updateOrderStatus(command);

        // Then
        assertEquals(50L, availableCopiesOf(effectiveJava));
        assertEquals(OrderStatus.CANCELED, queryOrderService.findById(orderId).get().getStatus());
    }

    @Test
    public void userCannotRevokePaidOrder() {
        // Given
        Book effectiveJava = givenEffectiveJava(50L);
        String recipient = "marek@example.org";
        Long orderId = placeOrder(effectiveJava.getId(), 15, recipient);
        assertEquals(35L, availableCopiesOf(effectiveJava));
        UpdateStatusCommand commandPaid = new UpdateStatusCommand(orderId, OrderStatus.PAID,recipient);
        service.updateOrderStatus(commandPaid);

        // When
        UpdateStatusCommand commandCanceled = new UpdateStatusCommand(orderId, OrderStatus.CANCELED,recipient);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.updateOrderStatus(commandCanceled);
        });

        // Then
        assertTrue(exception.getMessage().contains("Unable to mark " + OrderStatus.PAID + " order as " + OrderStatus.CANCELED));
    }

    @Test
    public void userCannotRevokeShippedOrder() {
        // Given
        Book effectiveJava = givenEffectiveJava(50L);
        String recipient = "marek@example.org";
        Long orderId = placeOrder(effectiveJava.getId(), 15, recipient);
        assertEquals(35L, availableCopiesOf(effectiveJava));
        UpdateStatusCommand commandPaid = new UpdateStatusCommand(orderId, OrderStatus.PAID,recipient);
        UpdateStatusCommand commandShipped = new UpdateStatusCommand(orderId, OrderStatus.SHIPPED,recipient);
        service.updateOrderStatus(commandPaid);
        service.updateOrderStatus(commandShipped);

        // When
        UpdateStatusCommand commandCanceled = new UpdateStatusCommand(orderId, OrderStatus.CANCELED,recipient);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.updateOrderStatus(commandCanceled);
        });

        // Then
        assertTrue(exception.getMessage().contains("Unable to mark " + OrderStatus.SHIPPED + " order as " + OrderStatus.CANCELED));
    }

    @Test
    public void userCannotOrderNoExistingBooks() {
        // Given
        Book effectiveJava = givenEffectiveJava(50L);
        Long bookId = effectiveJava.getId();
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient())
                .item(new OrderItemCommand(bookId + 1, 15))
                .build();

        // Then
        assertThrows(EntityNotFoundException.class, () -> {
            service.placeOrder(command);
        });
    }

    @Test
    public void userCannotOrderNegativeNumberOfBooks() {
        // Given
        Book effectiveJava = givenEffectiveJava(50L);
        Long bookId = effectiveJava.getId();
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient())
                .item(new OrderItemCommand(bookId, -15))
                .build();

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.placeOrder(command);
        });

        // Then
        assertTrue(exception.getMessage().contains("The number of copies of the requested book" + bookId +
                " cannot be less than zero"));
    }

    @Test
    public void userCannotRevokeOtherUsersOrder() {
        // Given
        Book effectiveJava = givenEffectiveJava(50L);
        String adam = "adam@example.org ";
        Long orderId = placeOrder(effectiveJava.getId(), 15, adam);
        assertEquals(35L, availableCopiesOf(effectiveJava));

        // When
        UpdateStatusCommand command = new UpdateStatusCommand(orderId, OrderStatus.CANCELED, "marek@example.org");
        service.updateOrderStatus(command);

        // Then
        assertEquals(35L, availableCopiesOf(effectiveJava));
        assertEquals(OrderStatus.NEW, queryOrderService.findById(orderId).get().getStatus());
    }

    @Test
    // Todo-Maciek" poprawić w module security
    public void adminCanRevokeOtherUsersOrder() {
        // Given
        Book effectiveJava = givenEffectiveJava(50L);
        String marek = "marek@example.org ";
        Long orderId = placeOrder(effectiveJava.getId(), 15, marek);
        assertEquals(35L, availableCopiesOf(effectiveJava));

        // When
        String admin = "admin@example.org";
        UpdateStatusCommand command = new UpdateStatusCommand(orderId, OrderStatus.CANCELED, admin);
        service.updateOrderStatus(command);

        // Then
        assertEquals(50L, availableCopiesOf(effectiveJava));
        assertEquals(OrderStatus.CANCELED, queryOrderService.findById(orderId).get().getStatus());
    }

    @Test
    public void adminCanMarkOrderAsPaid() {
        // Given
        Book effectiveJava = givenEffectiveJava(50L);
        String recipient = "marek@example.org";
        Long orderId = placeOrder(effectiveJava.getId(), 15, recipient);
        assertEquals(35L, availableCopiesOf(effectiveJava));

        // When
        String admin = "admin@example.org";
        UpdateStatusCommand command = new UpdateStatusCommand(orderId, OrderStatus.PAID, admin);
        service.updateOrderStatus(command);

        // Then
        assertEquals(35L, availableCopiesOf(effectiveJava));
        assertEquals(OrderStatus.PAID, queryOrderService.findById(orderId).get().getStatus());
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

    @Test
    public void shippingCostsAreAddedToTotalOrderPrice() {
        // Given
        Book book = givenBook(50L, "49.90");

        // When
        Long orderId = placeOrder(book.getId(), 1);

        // Then
        assertEquals("59.80", orderOf(orderId).getFinalPrice().toPlainString());
    }

    @Test
    public void shippingCostsArDiscountedOver100zlotys() {
        // Given
//        Book book = givenBook(50L, "49.90");

        // When
//        Long orderId = placeOrder(book.getId(), 3);

        // Then
//        RichOrder order = orderOf(orderId);
//        assertEquals("149.70", order.getFinalPrice().toPlainString());
//        assertEquals("149.70", order.getOrderPrice().getItemsPrice().toPlainString());
    }

    @Test
    public void cheapestBookIsHalfPricedWhenTotalOver200zlotys() {
        // Given
//        Book book = givenBook(50L, "49.90");

        // When
//        Long orderId = placeOrder(book.getId(), 5);

        // Then
//        RichOrder order = orderOf(orderId);
//        assertEquals("224.55", order.getFinalPrice().toPlainString());
    }

    @Test
    public void cheapestBookIsFreeWhenTotalOver400zlotys() {
        // Given
//        Book book = givenBook(50L, "49.90");

        // When
//        Long orderId = placeOrder(book.getId(), 10);

        // Then
//        RichOrder order = orderOf(orderId);
//        assertEquals("449.10", orderOf(orderId).getFinalPrice().toPlainString());
    }

    private  RichOrder orderOf(Long orderId) {
        return queryOrderService.findById(orderId).get();
    }

    private Book givenBook(long available, String price) {
        return bookJpaRepository.save(new Book("Java Concurrency in Practice", 2006, new BigDecimal("99,90"), available));
    }

    private Long placeOrder(Long bookId, int copies) {
        return placeOrder(bookId, copies, "john@example.org");
    }

    private Long placeOrder(Long bookId, int copies, String recipient) {
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient(recipient))
                .item(new OrderItemCommand(bookId, copies))
                .build();
        return service.placeOrder(command).getRight();
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
    
    private Recipient recipient(String email) {
        return Recipient.builder().email(email).build();
    }

    private Long availableCopiesOf(Book effectiveJava) {
        return catalogUseCase.findById(effectiveJava.getId())
                .get()
                .getAvailable();
    }

}
