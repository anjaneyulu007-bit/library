package com.library.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.library.dto.BookDTO;
import com.library.dto.BorrowRequestDTO;
import com.library.exception.BookNotAvailableException;
import com.library.exception.ResourceNotFoundException;
import com.library.model.Book;
import com.library.model.Borrower;
import com.library.repository.BookRepository;
import com.library.repository.BorrowerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookService {

	private final BookRepository bookRepository = null;
	private final BorrowerRepository borrowerRepository = null;

	public Book registerBook(BookDTO bookDTO) {
		Book book = new Book();
		book.setIsbn(bookDTO.getIsbn());
		book.setTitle(bookDTO.getTitle());
		book.setAuthor(bookDTO.getAuthor());
		book.setAvailable(true);
		return bookRepository.save(book);
	}

	public List<Book> getAllBooks() {
		return bookRepository.findAll();
	}

	public List<Book> getAvailableBooks() {
		return bookRepository.findByAvailable(true);
	}

	@Transactional
	public Book borrowBook(BorrowRequestDTO borrowRequest) {
		Book book = bookRepository.findByIdAndAvailable(borrowRequest.getBookId(), true)
				.orElseThrow(() -> new BookNotAvailableException(
						"Book with ID " + borrowRequest.getBookId() + " is not available for borrowing"));

		Borrower borrower = borrowerRepository.findById(borrowRequest.getBorrowerId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"Borrower with ID " + borrowRequest.getBorrowerId() + " not found"));

		book.setAvailable(false);
		book.setBorrower(borrower);
		return bookRepository.save(book);
	}

	@Transactional
	public Book returnBook(Long bookId) {
		Book book = bookRepository.findById(bookId)
				.orElseThrow(() -> new ResourceNotFoundException("Book with ID " + bookId + " not found"));

		if (book.isAvailable()) {
			throw new IllegalStateException("Book with ID " + bookId + " is not currently borrowed");
		}

		book.setAvailable(true);
		book.setBorrower(null);
		return bookRepository.save(book);
	}
}