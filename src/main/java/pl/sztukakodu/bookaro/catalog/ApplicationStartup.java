package pl.sztukakodu.bookaro.catalog;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.sztukakodu.bookaro.catalog.application.CatalogController;
import pl.sztukakodu.bookaro.catalog.domain.Book;

import java.util.List;

@Component
public class ApplicationStartup implements CommandLineRunner {
    private final CatalogController catalogController;
    private final String title;
    private final Long limit;

    public ApplicationStartup(
            CatalogController catalogController,
            @Value("${bookaro.catalog.query:Pan}") String title,
            @Value("${bookaro.catalog.limit:3}") Long limit) {
        this.catalogController = catalogController;
        this.title = title;
        this.limit = limit;
    }

    @Override
    public void run(String... args) throws Exception {
        List<Book> books = catalogController.findByTitle(title);
        books.stream().limit(limit).forEach(System.out::println);
    }
}
