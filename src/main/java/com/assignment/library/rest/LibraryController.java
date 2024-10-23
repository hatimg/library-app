package com.assignment.library.rest;

import com.assignment.library.model.dto.BookDto;
import com.assignment.library.service.LibraryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/library")
@Tag(name = "Library Management")
public class LibraryController {

    private final LibraryService libraryService;

    @Autowired
    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @PostMapping("/book")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "Add a book to the library")
    @ApiResponses(value = {
            @ApiResponse(responseCode ="201", description = "SUCCESS: Book successfully added"),
            @ApiResponse(responseCode = "409", description = "FAILURE: Book already exists"),
    })
    public String addBook(@RequestBody BookDto bookDto) {
        return libraryService.addBook(bookDto);
    }

    @GetMapping(name="Find a book by ISBN", value="/book/{isbn}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Find a specific book using ISBN")
    public BookDto findBookByIsbn(@PathVariable String isbn) {
        return libraryService.findBookByIsbn(isbn);
    }

    @GetMapping(name="Find books by Author", value="/author/{author}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Find all books for an author")
    public List<BookDto> findBooksByAuthor(@PathVariable String author) {
        return libraryService.findBooksByAuthor(author);
    }

}
