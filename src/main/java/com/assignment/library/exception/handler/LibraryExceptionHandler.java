package com.assignment.library.exception.handler;

import com.assignment.library.exception.BookAlreadyExistsException;
import com.assignment.library.exception.BookNotAvailableException;
import com.assignment.library.exception.BookNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

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

    @ExceptionHandler(BookNotAvailableException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleBookNotAvailableException(BookNotAvailableException exception) {
        log.error(exception.getMessage(), exception) ;
        return exception.getMessage();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationErrors(MethodArgumentNotValidException exception) {
        Map<String, String> errorMap = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(
                error -> errorMap.put(error.getField(), error.getDefaultMessage())
        );
        log.error("Validations failed with following errors : {}", errorMap);
        return errorMap;
    }



}
