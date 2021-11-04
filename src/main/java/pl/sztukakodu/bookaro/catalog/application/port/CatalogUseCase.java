package pl.sztukakodu.bookaro.catalog.application.port;

import lombok.Value;
import pl.sztukakodu.bookaro.catalog.domain.Book;

import java.util.List;
import java.util.Optional;

public interface CatalogUseCase {
    List<Book> findByTitle(String title);

    List<Book> findAll();

    Optional<Book> findOneByTitleAndAuthor(String title, String author);

    void addBook(CreateBookCommand command);

    void removeById(long id);

    void updateBook();

    @Value
    class CreateBookCommand {
        String title;
        String author;
        Integer year;
    }
}
