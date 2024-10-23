package com.assignment.library.rest;

import com.assignment.library.model.dto.BookDto;
import com.assignment.library.service.LibraryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * This IT specifically is to test the validations. Since only the web layer is needed to validate the failures,
 * this test uses @WebMvcTest, which does not spin up the entire server but only tests the controller layer.
 */
@WebMvcTest
public class LibraryControllerValidationIT {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LibraryService libraryService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        reset(libraryService);
    }

    @Test
    void shouldGiveErrorMessage_WhenIsbnIsMissing() throws Exception {
        BookDto bookDto = new BookDto("", "Some Title", "John Doe", 2020, 5);

        mockMvc.perform(post("/library/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isbn").value("Book ISBN is required"));
    }

    @Test
    void shouldGiveErrorMessage_WhenTitleIsMissing() throws Exception {
        BookDto bookDto = new BookDto("1234567890", "", "John Doe", 2020, 5);

        mockMvc.perform(post("/library/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Book title is required"));
    }

    @Test
    void shouldGiveErrorMessage_WhenAuthorIsMissing() throws Exception {
        BookDto bookDto = new BookDto("1234567890", "Some Title", "", 2020, 5);

        mockMvc.perform(post("/library/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.author").value("Book author is required"));
    }

    @Test
    void shouldGiveErrorMessage_WhenPublicationYearIsInvalid() throws Exception {
        BookDto bookDto = new BookDto("1234567890", "Some Title", "John Doe", 1949, 5);

        mockMvc.perform(post("/library/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.publicationYear").value("must be greater than or equal to 1950"));
    }

    @Test
    void shouldGiveErrorMessage_WhenAvailableCopiesIsNegative() throws Exception {
        BookDto bookDto = new BookDto("1234567890", "Some Title", "John Doe", 2020, -1);

        mockMvc.perform(post("/library/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.availableCopies").value("Number of available copies should be 0 or more"));
    }

    @Test
    void shouldGiveAllFailureErrorMessages_WhenMultipleFieldsAreInvalid() throws Exception {
        BookDto bookDto = new BookDto("", "", null, 6, -1);

        mockMvc.perform(post("/library/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isbn").value("Book ISBN is required"))
                .andExpect(jsonPath("$.title").value("Book title is required"))
                .andExpect(jsonPath("$.author").value("Book author is required"))
                .andExpect(jsonPath("$.publicationYear").value("must be greater than or equal to 1950"))
                .andExpect(jsonPath("$.availableCopies").value("Number of available copies should be 0 or more"));
    }
}
