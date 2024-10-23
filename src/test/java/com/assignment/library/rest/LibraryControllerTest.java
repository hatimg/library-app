package com.assignment.library.rest;

import com.assignment.library.model.dto.BookDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.*;

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

    @Test
    void shouldFindABookByISBN() {
        BookDto bookDto = createBook("3333", AUTHOR_NAME, 5);
        ResponseEntity<BookDto> response = testRestTemplate.getForEntity(format("/library/book/%s", "3333"), BookDto.class);
        assertEquals(OK, response.getStatusCode());
        assertEquals(bookDto, response.getBody());
    }

    @Test
    void shouldReturnAMessage_IfBookIsNotFound() {
        ResponseEntity<String> response = testRestTemplate.getForEntity(format("/library/book/%s", "0000"), String.class);
        assertEquals(NOT_FOUND, response.getStatusCode());
        assertEquals("Book with ISBN 0000 not found.", response.getBody());
    }

    @Test
    void shouldFindBooksForAuthor() {
        createBook("4444", "Joe Blogs", 5);
        BookDto bookDto = createBook("5555", "Joe Blogs", 5);
        var response = testRestTemplate.getForEntity(format("/library/author/%s", bookDto.author()), List.class);
        assertEquals(OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void shouldReturnAMessage_IfFindBooksByAuthorFindsNoBooksForTheAuthor() {
        ResponseEntity<String> response = testRestTemplate.getForEntity(format("/library/author/%s", "John Smith"), String.class);
        assertEquals(NOT_FOUND, response.getStatusCode());
        assertEquals("Books for author John Smith not found.", response.getBody());
    }

    @Test
    void shouldDeleteABook() {
        BookDto bookDto = createBook("2222", AUTHOR_NAME, 5);
        ResponseEntity<String> deleteResponse = testRestTemplate.exchange(format("/library/book/%s", "2222"), HttpMethod.DELETE, HttpEntity.EMPTY, String.class);
        assertEquals(OK, deleteResponse.getStatusCode());
        assertEquals(format("Book with ISBN %s deleted.", "2222"), deleteResponse.getBody());

    }

    @Test
    void deleteShouldGiveMessage_IfBookNotPresent() {
        ResponseEntity<String> deleteResponse = testRestTemplate.exchange(format("/library/book/%s", "999999"), HttpMethod.DELETE, HttpEntity.EMPTY, String.class);
        assertEquals(format("Book with ISBN %s not found.", "999999"), deleteResponse.getBody());
        assertEquals(NOT_FOUND, deleteResponse.getStatusCode());
    }

    protected BookDto createBook(String isbn, String author, int availableCopies) {
        BookDto bookDto = new BookDto(isbn, BOOK_TITLE, author, PUBLICATION_YEAR, availableCopies);
        testRestTemplate.postForEntity("/library/book", new HttpEntity<>(bookDto), String.class);
        return bookDto;
    }


}