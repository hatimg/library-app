package com.assignment.library.rest;

import com.assignment.library.model.dto.BookDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AbstractLibraryControllerIT {
    protected static final String AUTHOR_NAME = "My Author";
    protected static final String BOOK_TITLE = "My Title";
    protected static final int PUBLICATION_YEAR = 2022;

    @Autowired
    protected TestRestTemplate testRestTemplate;

    protected BookDto createBook(String isbn, String author, int availableCopies) {
        BookDto bookDto = new BookDto(isbn, BOOK_TITLE, author, PUBLICATION_YEAR, availableCopies);
        testRestTemplate.postForEntity("/library/book", new HttpEntity<>(bookDto), String.class);
        return bookDto;
    }

}
