package pl.sztukakodu.bookaro.catalog;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.sztukakodu.bookaro.catalog.application.port.CatalogUseCase;
import pl.sztukakodu.bookaro.catalog.application.port.CatalogUseCase.CreateBookCommand;
import pl.sztukakodu.bookaro.catalog.domain.Book;

import java.util.List;

@Component
public class ApplicationStartup implements CommandLineRunner {
    private final CatalogUseCase catalog;
    private final String title;
    private final Long limit;

    public ApplicationStartup(
            CatalogUseCase catalog,
            @Value("${bookaro.catalog.query.title}") String title,
            @Value("${bookaro.catalog.query.limit}") Long time) {
        this.catalog = catalog;
        this.title = title;
        this.limit = time;
    }

    @Override
    public void run(String... args) {
        initData();
        findByTitle();
    }

    private void initData() {
        catalog.addBook(new CreateBookCommand("Pan Tadeusz", "Ada Mickiewicz", 1834));
        catalog.addBook(new CreateBookCommand("Ogniem i Mieczem", "Henryk Sienkiewicz", 1884));
        catalog.addBook(new CreateBookCommand("Chłopi", "Władysław Reymont", 1904));
        catalog.addBook(new CreateBookCommand("Pan Wołodyjski", "Henryk Sienkiewicz", 1899));
    }

    private void findByTitle() {
        List<Book> booksByTitle = catalog.findByTitle(title);
        booksByTitle.stream().limit(limit).forEach(System.out::println);
    }
}
