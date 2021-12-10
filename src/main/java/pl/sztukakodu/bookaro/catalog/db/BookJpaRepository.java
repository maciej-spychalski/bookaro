package pl.sztukakodu.bookaro.catalog.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.sztukakodu.bookaro.catalog.domain.Book;

import java.util.List;
import java.util.Optional;

public interface BookJpaRepository extends JpaRepository<Book, Long> {

    @Query("SELECT b FROM Book b JOIN FETCH b.authors")
    List<Book> findAllEager();

    List<Book> findByAuthors_firstNameContainsIgnoreCaseOrAuthors_lastNameContainsIgnoreCase(String firstName, String lastName);

    List<Book> findByTitleStartsWithIgnoreCase(String title);

    Optional<Book> findDistinctFirstByTitle(String title);

    @Query(" SELECT b FROM Book b JOIN b.authors a " +
            " WHERE " +
            " lower(a.firstName) LIKE lower(concat('%', :author, '%')) " +
            " OR lower(a.lastName) LIKE lower(concat('%', :author, '%')) "
    )
    List<Book> findByAuthor(@Param("author") String author);

    @Query(" SELECT b FROM Book b JOIN b.authors a " +
            " WHERE " +
            " lower(b.title) LIKE lower(concat('%', :title, '%')) " +
            " AND " +
            " (lower(a.firstName) LIKE lower(concat('%', :author, '%')) OR " +
            " lower(a.lastName) LIKE lower(concat('%', :author, '%'))) "
    )
    List<Book> findByTitleAndAuthor(@Param("title") String title, @Param("author") String author);

}

