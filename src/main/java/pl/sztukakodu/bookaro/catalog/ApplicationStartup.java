package pl.sztukakodu.bookaro.catalog;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.sztukakodu.bookaro.catalog.application.CatalogController;
import pl.sztukakodu.bookaro.catalog.domain.Book;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ApplicationStartup implements CommandLineRunner {
    private final CatalogController catalogController;

    @Override
    public void run(String... args) throws Exception {
        List<Book> books = catalogController.findByTitle("Pan");
        books.forEach(System.out::println);
    }
}
