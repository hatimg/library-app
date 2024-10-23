package com.assignment.library.model.dto;

import jakarta.validation.constraints.*;

/**
 * Data object to carry around the book data across the system above the Data Layer
 */
public record BookDto(
        @NotBlank(message="Book ISBN is required")
        String isbn,

        @NotBlank(message="Book title is required")
        String title,

        @NotBlank(message="Book author is required")
        String author,

        @Digits(fraction=0, integer = 4, message = "Book publication year should be a valid year")
        @Min(value=1950)
        @Max(value=2024)
        int publicationYear,

        @PositiveOrZero(message = "Number of available copies should be 0 or more")
        int availableCopies,

        Long version) {

    public BookDto(String isbn, String title, String author, int publicationYear, int availableCopies) {
        this(isbn, title, author, publicationYear, availableCopies, 0L);

    }
}
