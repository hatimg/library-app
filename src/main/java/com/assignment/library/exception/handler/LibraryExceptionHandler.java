package com.assignment.library.exception.handler;

import com.assignment.library.exception.BookAlreadyExistsException;
import com.assignment.library.exception.BookNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
@Slf4j
public class LibraryExceptionHandler {

    @ExceptionHandler(BookAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleBookAlreadyExistsException(BookAlreadyExistsException exception) {
        log.error(exception.getMessage(), exception) ;
        return exception.getMessage();
    }

    @ExceptionHandler(BookNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public String handleBookNotFoundException(BookNotFoundException exception) {
        log.error(exception.getMessage(), exception) ;
        return exception.getMessage();
    }


}
