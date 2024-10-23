package com.assignment.library.rest;

import com.assignment.library.model.dto.BookDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LibraryControllerConcurrencyIT extends AbstractLibraryControllerIT {

    @Test
    void borrowABook_ConcurrencyTest() throws Exception {

        BookDto bookDto = createBook("1234", AUTHOR_NAME, 1);

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Callable<ResponseEntity<String>> firstRequestToBorrow = () -> testRestTemplate.exchange(format("/library/book/%s/borrow", bookDto.isbn()), HttpMethod.PUT, HttpEntity.EMPTY, String.class);
        Callable<ResponseEntity<String>> secondRequestToBorrow = () -> testRestTemplate.exchange(format("/library/book/%s/borrow", bookDto.isbn()), HttpMethod.PUT, HttpEntity.EMPTY, String.class);

        List<Future<ResponseEntity<String>>> futures = executorService.invokeAll(List.of(firstRequestToBorrow, secondRequestToBorrow));

        AtomicInteger successCounter = new AtomicInteger();
        AtomicInteger errorCounter = new AtomicInteger();

        futures.forEach(future -> {
                    try {
                        ResponseEntity<String> response = future.get();
                        String responseBody = response.getBody();
                        System.out.println(responseBody);
                        if (responseBody != null && responseBody.contains("Row was updated or deleted by another transaction")) {
                            errorCounter.getAndIncrement();
                        } else {
                            successCounter.getAndIncrement();
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        assertEquals(1, successCounter.get());
        assertEquals(1, errorCounter.get());
        executorService.shutdown();
    }

    @Test
    void returnABook_ConcurrencyTest() throws Exception {

        BookDto bookDto = createBook("9876", AUTHOR_NAME, 1);

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Callable<ResponseEntity<String>> firstRequestToBorrow = () -> testRestTemplate.exchange(format("/library/book/%s/return", bookDto.isbn()), HttpMethod.PUT, HttpEntity.EMPTY, String.class);
        Callable<ResponseEntity<String>> secondRequestToBorrow = () -> testRestTemplate.exchange(format("/library/book/%s/return", bookDto.isbn()), HttpMethod.PUT, HttpEntity.EMPTY, String.class);

        List<Future<ResponseEntity<String>>> futures = executorService.invokeAll(List.of(firstRequestToBorrow, secondRequestToBorrow));

        AtomicInteger successCounter = new AtomicInteger();
        AtomicInteger errorCounter = new AtomicInteger();

        futures.forEach(future -> {
                    try {
                        ResponseEntity<String> response = future.get();
                        String responseBody = response.getBody();
                        System.out.println(responseBody);
                        if (responseBody != null && responseBody.contains("Row was updated or deleted by another transaction")) {
                            errorCounter.getAndIncrement();
                        } else {
                            successCounter.getAndIncrement();
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        assertEquals(1, successCounter.get());
        assertEquals(1, errorCounter.get());
        executorService.shutdown();
    }



}
