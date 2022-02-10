package pl.sztukakodu.bookaro.catalog.web;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pl.sztukakodu.bookaro.catalog.application.port.CatalogUseCase;
import pl.sztukakodu.bookaro.catalog.domain.Book;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({CatalogController.class})
@ActiveProfiles("test")
@WithMockUser
class CatalogControllerWebTest {

    @MockBean
    CatalogUseCase catalogUseCase;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void shouldGetAllBooks() throws Exception {
        // Given
        Book effective = new Book("Effective Java", 2005, new BigDecimal("99.00"),50L);
        Book concurrency = new Book("Java Concurrency", 2003, new BigDecimal("87.00"),50L);
        Mockito.when(catalogUseCase.findAll()).thenReturn(List.of(effective, concurrency));

        // Expect
        mockMvc.perform(get("/catalog"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
}