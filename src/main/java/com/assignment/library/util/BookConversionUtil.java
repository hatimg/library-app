package com.assignment.library.util;

import com.assignment.library.model.dto.BookDto;
import com.assignment.library.model.entity.Book;

public class BookConversionUtil {
    private BookConversionUtil() {}

    public static Book convertFromBookDto(BookDto bookDto) {
        return Book.builder()
                .isbn(bookDto.isbn())
                .title(bookDto.title())
                .author(bookDto.author())
                .publicationYear(bookDto.publicationYear())
                .availableCopies(bookDto.availableCopies())
                .version(bookDto.version())
                .build();
    }

    public static BookDto convertToBookDto(Book book) {
        return new BookDto(
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor(),
                book.getPublicationYear(),
                book.getAvailableCopies(),
                book.getVersion());
    }
}
