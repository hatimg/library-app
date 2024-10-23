package com.assignment.library.exception;

import com.assignment.library.model.entity.Book;

public class BookAlreadyExistsException extends RuntimeException {
    public BookAlreadyExistsException(String message) {
        super(message);
    }
}
