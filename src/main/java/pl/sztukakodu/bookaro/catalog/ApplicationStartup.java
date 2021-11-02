package pl.sztukakodu.bookaro.catalog;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.sztukakodu.bookaro.catalog.application.CatalogController;
import pl.sztukakodu.bookaro.catalog.domain.Book;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ApplicationStartup implements CommandLineRunner {
    private final CatalogController catalogController;
    @Value("${bookaro.catalog.query.title}")
    private String title;
    @Value("${bookaro.catalog.query.author}")
    private String author;
    @Value("${bookaro.catalog.query.limit:3}")
    private Long limit;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\nFind by title: " + title);
        List<Book> booksByTitle = catalogController.findByTitle(title);
        booksByTitle.stream().limit(limit).forEach(System.out::println);

        System.out.println("\nFind by author: " + author);
        List<Book> booksByAuthor = catalogController.findByAuthor(author);
        booksByAuthor.stream().limit(limit).forEach(System.out::println);
    }
}
