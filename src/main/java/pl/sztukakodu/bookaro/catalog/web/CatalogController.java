package pl.sztukakodu.bookaro.catalog.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.sztukakodu.bookaro.catalog.application.port.CatalogUseCase;
import pl.sztukakodu.bookaro.catalog.application.port.CatalogUseCase.CreateBookCommand;
import pl.sztukakodu.bookaro.catalog.domain.Book;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RequestMapping("/catalog")
@RestController
@AllArgsConstructor
public class CatalogController {
    private final CatalogUseCase catolog;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Book> getAll(
            @RequestParam Optional<String> title,
            @RequestParam Optional<String> author) {
        if (title.isPresent() && author.isPresent()) {
            return catolog.findByTitleAndAuthor(title.get(), author.get());
        }
        else if (title.isPresent()) {
            return catolog.findByTitle(title.get());
        }
        else if (author.isPresent()) {
            return catolog.findByAuthor(author.get());
        }
        return catolog.findAll();
    }

        @GetMapping("/{id}")
//    public ResponseEntity<?> getById(@PathVariable Long id) {
    public ResponseEntity<Book> getById(@PathVariable Long id) {
        return catolog
                .findById(id)
//                .map(book -> ResponseEntity.ok(book))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> addBook(@RequestBody RestCreateBookCommand command) {
        Book book = catolog.addBook(command.toCommand());
        return ResponseEntity.created(createdBookUri(book)).build();
    }

    private URI createdBookUri(Book book) {
        return ServletUriComponentsBuilder
                .fromCurrentRequestUri().path("/" + book.getId().toString()).build().toUri();
    }

    @Data
    private static class RestCreateBookCommand {
        private String title;
        private String author;
        private Integer year;
        private BigDecimal price;

        CreateBookCommand toCommand() {
            return new CreateBookCommand(
                    title, author, year, price
            );
        }
    }

}