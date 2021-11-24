package pl.sztukakodu.bookaro.order.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sztukakodu.bookaro.order.application.port.PlaceOrderUseCase;
import pl.sztukakodu.bookaro.order.domain.Order;
import pl.sztukakodu.bookaro.order.domain.OrderRepository;
import pl.sztukakodu.bookaro.order.domain.OrderStatus;

@Service
@AllArgsConstructor
public class PlaceOrderService implements PlaceOrderUseCase {
    private final OrderRepository repository;

    @Override
    public PlaceOrderResponse placeOrder(PlaceOrderCommand command) {
        Order order = Order
                .builder()
                .recipient(command.getRecipient())
                .items(command.getItems())
                .build();
        Order save = repository.save(order);
        return PlaceOrderResponse.success(save.getId());
    }

    @Override
    public void updateOrderStatus(Long id, OrderStatus status) {
        Order order = repository.findById(id).orElse(null);
        order.setStatus(status);
        repository.save(order);
    }
}
