package com.assignment.library.service;

import com.assignment.library.exception.BookAlreadyExistsException;
import com.assignment.library.exception.BookNotAvailableException;
import com.assignment.library.exception.BookNotFoundException;
import com.assignment.library.model.dto.BookDto;
import com.assignment.library.model.entity.Book;
import com.assignment.library.repository.BookRepository;
import com.assignment.library.util.CacheEvictionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibraryServiceImplTest {

    private LibraryService libraryService;

    @Mock
    private BookRepository mockBookRepository;

    @Mock
    private CacheEvictionHandler mockCacheEvictionHandler;

    @BeforeEach
    public void setUp() {
        libraryService = new LibraryServiceImpl(mockBookRepository, mockCacheEvictionHandler);
    }

    @Test
    public void shouldAddBook_WhenNotFound() {
        BookDto bookDto = new BookDto("123", "Title", "Joe Blogs", 2020, 5);
        Book book = new Book("123", "Title", "Joe Blogs", 2020, 5, 0L);

        when(mockBookRepository.existsById(book.getIsbn())).thenReturn(false);
        when(mockBookRepository.save(any(Book.class))).thenReturn(book);

        String result = libraryService.addBook(bookDto);

        assertEquals("123", result);
        verify(mockBookRepository).save(any(Book.class));
    }

    @Test
    public void shouldThrowException_WhenBookAlreadyExistsWhileAdding() {
        BookDto bookDto = new BookDto("123", "Title", "Will Smith", 2024, 5);

        when(mockBookRepository.existsById(bookDto.isbn())).thenReturn(true);

        assertThrows(BookAlreadyExistsException.class, () -> libraryService.addBook(bookDto));
        verify(mockBookRepository, never()).save(any(Book.class));
    }

    @Test
    void shouldFindBookByIsbn() {
        String isbn = "1234567890";
        Book book = new Book(isbn, "Some Title", "John Doe", 2020, 5, 0L);
        BookDto bookDto = new BookDto(isbn, "Some Title", "John Doe", 2020, 5);

        when(mockBookRepository.findById(isbn)).thenReturn(Optional.of(book));

        BookDto result = libraryService.findBookByIsbn(isbn);

        assertEquals(bookDto, result);
    }

    @Test
    void shouldThrowException_WhenBookIsNotFound() {
        String isbn = "1234567890";

        when(mockBookRepository.findById(isbn)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> libraryService.findBookByIsbn(isbn));
    }

    @Test
    void shouldFindBooksByAuthorSuccessfully() {
        String author = "John Doe";
        Book book1 = new Book("1234567890", "Some Title 1", author, 2020, 5, 0L);
        Book book2 = new Book("0987654321", "Some Title 2", author, 2021, 3, 0L);
        List<Book> books = Arrays.asList(book1, book2);

        when(mockBookRepository.findByAuthor(author)).thenReturn(books);

        List<BookDto> result = libraryService.findBooksByAuthor(author);

        assertEquals(2, result.size());
        assertEquals("1234567890", result.get(0).isbn());
        assertEquals("Some Title 1", result.get(0).title());
        assertEquals("0987654321", result.get(1).isbn());
        assertEquals("Some Title 2", result.get(1).title());
        verify(mockBookRepository).findByAuthor(author);
    }

    @Test
    void shouldThrowException_WhenNoBooksFoundByAuthor() {
        String author = "Unknown Author";

        when(mockBookRepository.findByAuthor(author)).thenReturn(List.of()); // Return an empty list

        assertThrows(BookNotFoundException.class, () -> libraryService.findBooksByAuthor(author));
        verify(mockBookRepository).findByAuthor(author);
    }

    @Test
    void shouldRemoveBookSuccessfully() {
        String isbn = "123";
        Book book = new Book(isbn, "Title", "Will Smith", 2024, 5, 0L);

        when(mockBookRepository.findById(isbn)).thenReturn(Optional.of(book));

        String result = libraryService.removeBook(isbn);

        assertEquals(format("Book with ISBN %s deleted.", isbn), result);
        verify(mockBookRepository).delete(book);
        verify(mockCacheEvictionHandler).evictCache(eq("booksByAuthorCache"), eq("Will Smith"));
    }

    @Test
    void shouldThrowException_WhenRemovingNonExistentBook() {
        String isbn = "1234567890";

        when(mockBookRepository.findById(isbn)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> libraryService.removeBook(isbn));
    }

    @Test
    void shouldBorrowBookSuccessfully() {
        String isbn = "1234567890";
        Book book = new Book(isbn, "Some Title", "John Doe", 2020, 5, 0L);

        when(mockBookRepository.findById(isbn)).thenReturn(Optional.of(book));
        BookDto bookDto = new BookDto(isbn, "Some Title", "John Doe", 2020, 4);
        when(mockBookRepository.save(any(Book.class))).thenReturn(book);

        BookDto result = libraryService.borrowBook(isbn);

        assertEquals(bookDto, result);
        assertEquals(4, book.getAvailableCopies());
    }


    @Test
    void shouldReturnBookSuccessfully() {
        String isbn = "1234567890";
        Book book = new Book(isbn, "Some Title", "John Doe", 2020, 5, 0L);

        when(mockBookRepository.findById(isbn)).thenReturn(Optional.of(book));
        when(mockBookRepository.save(any(Book.class))).thenReturn(book);

        BookDto result = libraryService.returnBook(isbn);

        assertEquals(6, book.getAvailableCopies());
    }

    @Test
    void shouldThrowException_IfBorrowingUnavailableBook() {
        String isbn = "1234567890";
        Book book = new Book(isbn, "Some Title", "John Doe", 2020, 0, 0L);

        when(mockBookRepository.findById(isbn)).thenReturn(Optional.of(book));

        assertThrows(BookNotAvailableException.class, () -> libraryService.borrowBook(isbn));
    }


}