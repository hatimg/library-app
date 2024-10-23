package com.assignment.library.service;

import com.assignment.library.exception.BookAlreadyExistsException;
import com.assignment.library.exception.BookNotFoundException;
import com.assignment.library.model.dto.BookDto;
import com.assignment.library.repository.BookRepository;
import com.assignment.library.util.BookConversionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
@Slf4j
public class LibraryServiceImpl implements LibraryService {

    private static final String BOOK_CACHE_NAME = "bookCache";
    private static final String BOOK_AUTHOR_CACHE_NAME = "booksByAuthorCache";

    private final BookRepository bookRepository;

    @Autowired
    public LibraryServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public String addBook(BookDto bookDto) {
        if (bookRepository.existsById(bookDto.isbn())) {
            throw new BookAlreadyExistsException(format("Book with ISBN %s already exists.", bookDto.isbn()));
        }

        return bookRepository.save(BookConversionUtil.convertFromBookDto(bookDto)).getIsbn();
    }

    @Override
    public String removeBook(String isbn) {
        return "";
    }

    @Override
    @Cacheable(value = BOOK_CACHE_NAME, key = "#isbn")
    public BookDto findBookByIsbn(String isbn) {
        return bookRepository.findById(isbn)
                .map(BookConversionUtil::convertToBookDto)
                .orElseThrow(() -> new BookNotFoundException(format("Book with ISBN %s not found.", isbn)));

    }

    @Override
    @Cacheable(value = BOOK_AUTHOR_CACHE_NAME, key = "#author")
    public List<BookDto> findBooksByAuthor(String author) {
        return bookRepository.findByAuthor(author)
                .stream()
                .map(BookConversionUtil::convertToBookDto)
                .collect(Collectors.collectingAndThen(Collectors.toList()
                        , books -> {
                            if (books.isEmpty()) { throw new BookNotFoundException(format("Books for author %s not found.", author)); }
                            return books;
                        }));
    }

    @Override
    public BookDto borrowBook(String isbn) {
        return null;
    }

    @Override
    public BookDto returnBook(String isbn) {
        return null;
    }
}
