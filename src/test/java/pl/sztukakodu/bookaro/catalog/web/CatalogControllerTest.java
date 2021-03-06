package pl.sztukakodu.bookaro.catalog.web;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.sztukakodu.bookaro.catalog.application.port.CatalogUseCase;
import pl.sztukakodu.bookaro.catalog.domain.Book;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CatalogController.class})
class CatalogControllerTest {

    @MockBean
    CatalogUseCase catalogUseCase;

    @Autowired
    CatalogController controller;

    @Test
    public void shouldGetAllBooks() {
        // Given
        Book effective = new Book("Effective Java", 2005, new BigDecimal("99.00"),50L);
        Book concurrency = new Book("Java Concurrency", 2003, new BigDecimal("87.00"),50L);
        Mockito.when(catalogUseCase.findAll()).thenReturn(List.of(effective, concurrency));

        // When
        List<RestBook> all = controller.getAll(mockRequest(), Optional.empty(), Optional.empty());

        // Then
        assertEquals(2, all.size());
    }

    private MockHttpServletRequest mockRequest() {
        return new MockHttpServletRequest();
    }

}