package com.assignment.library.rest;

import com.assignment.library.model.dto.BookDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LibraryControllerTest {

    protected static final String AUTHOR_NAME = "My Author";
    protected static final String BOOK_TITLE = "My Title";
    protected static final int PUBLICATION_YEAR = 2022;

    @Autowired
    protected TestRestTemplate testRestTemplate;

    @Test
    void shouldAddANewBook() {
        BookDto bookDto = new BookDto("9999", BOOK_TITLE, AUTHOR_NAME, PUBLICATION_YEAR, 5);
        ResponseEntity<String> response = testRestTemplate.postForEntity("/library/book", new HttpEntity<>(bookDto), String.class);
        assertEquals(bookDto.isbn(), response.getBody());
        assertEquals(CREATED, response.getStatusCode());
    }

    @Test
    void createShould_Not_AddABook_IfAlreadyPresent() {
        BookDto bookDto = createBook("1111", AUTHOR_NAME, 5);
        ResponseEntity<String> failureResponse = testRestTemplate.postForEntity("/library/book", new HttpEntity<>(bookDto), String.class);
        assertEquals(CONFLICT, failureResponse.getStatusCode());
    }


    protected BookDto createBook(String isbn, String author, int availableCopies) {
        BookDto bookDto = new BookDto(isbn, BOOK_TITLE, author, PUBLICATION_YEAR, availableCopies);
        testRestTemplate.postForEntity("/library/book", new HttpEntity<>(bookDto), String.class);
        return bookDto;
    }


}