package pl.sztukakodu.bookaro.catalog.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.sztukakodu.bookaro.catalog.application.port.CatalogUseCase;
import pl.sztukakodu.bookaro.catalog.application.port.CatalogUseCase.CreateBookCommand;
import pl.sztukakodu.bookaro.catalog.application.port.CatalogUseCase.UpdateBookCommand;
import pl.sztukakodu.bookaro.catalog.application.port.CatalogUseCase.UpdateBookCoverCommand;
import pl.sztukakodu.bookaro.catalog.application.port.CatalogUseCase.UpdateBookResponse;
import pl.sztukakodu.bookaro.catalog.domain.Book;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.*;

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
        } else if (title.isPresent()) {
            return catolog.findByTitle(title.get());
        } else if (author.isPresent()) {
            return catolog.findByAuthor(author.get());
        }
        return catolog.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getById(@PathVariable Long id) {
        return catolog
                .findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateBook(@PathVariable Long id, @Valid @RequestBody RestBookCommand command) {
        UpdateBookResponse response = catolog.updateBook(command.toUpdateCommand(id));
        if(!response.isSuccess()) {
            String message = String.join(", ", response.getErrors());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    @PutMapping(value = "/{id}/cover",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void addBookCover(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
        System.out.println("Got file: " + file.getOriginalFilename());
        catolog.updateBookCover(new UpdateBookCoverCommand(
                id,
                file.getBytes(),
                file.getContentType(),
                file.getOriginalFilename()
        ));
    }

    @DeleteMapping("/{id}/cover")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBookCover(@PathVariable Long id){
        catolog.removeBookCover(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> addBook(@Valid @RequestBody RestBookCommand command) {
        Book book = catolog.addBook(command.toCreateCommand());
        return ResponseEntity.created(createdBookUri(book)).build();
    }

    private URI createdBookUri(Book book) {
        return ServletUriComponentsBuilder
                .fromCurrentRequestUri().path("/" + book.getId().toString()).build().toUri();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        catolog.removeById(id);
    }

    @Data
    private static class RestBookCommand {
        @NotBlank(message = "Please provide a title")
        private String title;
        @NotBlank(message = "Please provide an author")
        private String author;
        @NotNull(message = "Please provide valid year")
        private Integer year;
        @NotNull(message = "Please provide valid price")
        @DecimalMin(value = "0.00", message = "Price must be grater than or equal to 0.00")
        private BigDecimal price;

        CreateBookCommand toCreateCommand() {
            return new CreateBookCommand(
                    title, author, year, price
            );
        }

        UpdateBookCommand toUpdateCommand(Long id) {
            return new UpdateBookCommand(id, title, author, year, price);
        }
    }

}
