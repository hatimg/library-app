package com.assignment.library.service;

import com.assignment.library.exception.BookAlreadyExistsException;
import com.assignment.library.model.dto.BookDto;
import com.assignment.library.model.entity.Book;
import com.assignment.library.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibraryServiceImplTest {

    private LibraryService libraryService;

    @Mock
    private BookRepository mockBookRepository;

    @BeforeEach
    public void setUp() {
        libraryService = new LibraryServiceImpl(mockBookRepository);
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

}