package pl.sztukakodu.bookaro.orders.application.port;

import pl.sztukakodu.bookaro.orders.application.RichOrder;

import java.util.List;
import java.util.Optional;

public interface QueryOrderUseCase {
    List<RichOrder> findAll();

    Optional<RichOrder> findById(Long id);

}
