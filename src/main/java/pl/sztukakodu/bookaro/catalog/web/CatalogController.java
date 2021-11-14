package pl.sztukakodu.bookaro.catalog.web;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.sztukakodu.bookaro.catalog.application.port.CatalogUseCase;
import pl.sztukakodu.bookaro.catalog.domain.Book;

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

}
