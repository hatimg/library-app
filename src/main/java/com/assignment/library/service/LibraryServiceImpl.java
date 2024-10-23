package com.assignment.library.service;

import com.assignment.library.exception.BookAlreadyExistsException;
import com.assignment.library.exception.BookNotAvailableException;
import com.assignment.library.exception.BookNotFoundException;
import com.assignment.library.model.dto.BookDto;
import com.assignment.library.model.entity.Book;
import com.assignment.library.repository.BookRepository;
import com.assignment.library.util.BookConversionUtil;
import com.assignment.library.util.CacheEvictionHandler;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
@Slf4j
public class LibraryServiceImpl implements LibraryService {

    private static final String BOOK_CACHE_NAME = "bookCache";
    private static final String BOOK_AUTHOR_CACHE_NAME = "booksByAuthorCache";

    private final BookRepository bookRepository;
    private final CacheEvictionHandler cacheEvictionHandler;

    @Autowired
    public LibraryServiceImpl(BookRepository bookRepository, CacheEvictionHandler cacheEvictionHandler) {
        this.bookRepository = bookRepository;
        this.cacheEvictionHandler = cacheEvictionHandler;
    }

    @Override
    public String addBook(BookDto bookDto) {
        if (bookRepository.existsById(bookDto.isbn())) {
            throw new BookAlreadyExistsException(format("Book with ISBN %s already exists.", bookDto.isbn()));
        }

        return bookRepository.save(BookConversionUtil.convertFromBookDto(bookDto)).getIsbn();
    }

    @Override
    @CacheEvict(value = BOOK_CACHE_NAME, key = "#isbn")
    public String removeBook(String isbn) {
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new BookNotFoundException(format("Book with ISBN %s not found.", isbn)));
        bookRepository.delete(book);
        cacheEvictionHandler.evictCache(BOOK_AUTHOR_CACHE_NAME, book.getAuthor());
        return format("Book with ISBN %s deleted.", isbn);
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
    @Transactional
    @CachePut(value = BOOK_CACHE_NAME, key = "#isbn")
    public BookDto borrowBook(String isbn) {
        Book book = findBook(isbn);
        if (book.getAvailableCopies() == 0) {
            throw new BookNotAvailableException(format("Book with ISBN %s no longer available.",isbn));
        }
        return updateBookAvailability(book, book.getAvailableCopies() - 1);
    }

    @Override
    @Transactional
    @CachePut(value = BOOK_CACHE_NAME, key = "#isbn")
    public BookDto returnBook(String isbn) {
        Book book = findBook(isbn);
        return updateBookAvailability(book, book.getAvailableCopies() + 1);
    }

    private BookDto updateBookAvailability(Book book, int availableCopies) {
        book.setAvailableCopies(availableCopies);
        Book updatedBook = bookRepository.save(book);
        cacheEvictionHandler.evictCache(BOOK_AUTHOR_CACHE_NAME, updatedBook.getAuthor());
        return BookConversionUtil.convertToBookDto(updatedBook);
    }

    private Book findBook(String isbn) {
        Optional<Book> bookOptional = bookRepository.findById(isbn);
        return bookOptional.orElseThrow(() -> new BookNotFoundException(format("Book with ISBN %s not found.", isbn)));

    }
}
