package com.assignment.library.service;

import com.assignment.library.model.dto.BookDto;
import jakarta.validation.constraints.NotNull;

import java.util.Collection;

public interface LibraryService {
    String addBook(@NotNull BookDto bookDto);

    String removeBook(String isbn);

    BookDto findBookByIsbn(String isbn);

    Collection<BookDto> findBooksByAuthor(String author);

    BookDto borrowBook(String isbn);

    BookDto returnBook(String isbn);

}
