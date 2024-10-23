package com.assignment.library.service;

import com.assignment.library.exception.BookAlreadyExistsException;
import com.assignment.library.model.dto.BookDto;
import com.assignment.library.model.entity.Book;
import com.assignment.library.repository.BookRepository;
import com.assignment.library.util.BookConversionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

import static java.lang.String.format;

@Service
@Slf4j
public class LibraryServiceImpl implements LibraryService {

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
    public BookDto findBookByIsbn(String isbn) {
        return null;
    }

    @Override
    public Collection<BookDto> findBooksByAuthor(String author) {
        return List.of();
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
