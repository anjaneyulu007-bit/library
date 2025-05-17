package com.library.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.library.dto.BookDTO;
import com.library.dto.BorrowRequestDTO;
import com.library.exception.BookNotAvailableException;
import com.library.exception.ResourceNotFoundException;
import com.library.model.Book;
import com.library.model.Borrower;
import com.library.repository.BookRepository;
import com.library.repository.BorrowerRepository;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

	@Mock
	private BookRepository bookRepository;

	@Mock
	private BorrowerRepository borrowerRepository;

	@InjectMocks
	private BookService bookService;

	@Test
	void registerBook_shouldSaveNewBook() {
		// Given
		BookDTO bookDTO = new BookDTO("978-3-16-148410-0", "Effective Java", "Joshua Bloch");
		Book expectedBook = new Book();
		expectedBook.setIsbn(bookDTO.getIsbn());
		expectedBook.setTitle(bookDTO.getTitle());
		expectedBook.setAuthor(bookDTO.getAuthor());
		expectedBook.setAvailable(true);

		when(bookRepository.save(any(Book.class))).thenReturn(expectedBook);

		// When
		Book result = bookService.registerBook(bookDTO);

		// Then
		assertNotNull(result);
		assertEquals(bookDTO.getIsbn(), result.getIsbn());
		assertEquals(bookDTO.getTitle(), result.getTitle());
		assertEquals(bookDTO.getAuthor(), result.getAuthor());
		assertTrue(result.isAvailable());
		verify(bookRepository, times(1)).save(any(Book.class));
	}

	@Test
	void borrowBook_shouldSuccessfullyBorrowAvailableBook() {
		// Given
		Long bookId = 1L;
		Long borrowerId = 1L;
		BorrowRequestDTO request = new BorrowRequestDTO(bookId, borrowerId);

		Book availableBook = new Book();
		availableBook.setId(bookId);
		availableBook.setAvailable(true);

		Borrower borrower = new Borrower();
		borrower.setId(borrowerId);

		when(bookRepository.findByIdAndAvailable(bookId, true)).thenReturn(Optional.of(availableBook));
		when(borrowerRepository.findById(borrowerId)).thenReturn(Optional.of(borrower));
		when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// When
		Book result = bookService.borrowBook(request);

		// Then
		assertNotNull(result);
		assertFalse(result.isAvailable());
		assertEquals(borrowerId, result.getBorrower().getId());
		verify(bookRepository, times(1)).findByIdAndAvailable(bookId, true);
		verify(borrowerRepository, times(1)).findById(borrowerId);
		verify(bookRepository, times(1)).save(availableBook);
	}

	@Test
	void borrowBook_shouldFailWhenBookNotAvailable() {
		// Given
		Long bookId = 1L;
		Long borrowerId = 1L;
		BorrowRequestDTO request = new BorrowRequestDTO(bookId, borrowerId);

		when(bookRepository.findByIdAndAvailable(bookId, true)).thenReturn(Optional.empty());

		// When & Then
		assertThrows(BookNotAvailableException.class, () -> bookService.borrowBook(request));

		verify(bookRepository, times(1)).findByIdAndAvailable(bookId, true);
		verify(borrowerRepository, never()).findById(anyLong());
		verify(bookRepository, never()).save(any());
	}

	@Test
	void borrowBook_shouldFailWhenBorrowerNotFound() {
		// Given
		Long bookId = 1L;
		Long borrowerId = 1L;
		BorrowRequestDTO request = new BorrowRequestDTO(bookId, borrowerId);

		Book availableBook = new Book();
		availableBook.setId(bookId);
		availableBook.setAvailable(true);

		when(bookRepository.findByIdAndAvailable(bookId, true)).thenReturn(Optional.of(availableBook));
		when(borrowerRepository.findById(borrowerId)).thenReturn(Optional.empty());

		// When & Then
		assertThrows(ResourceNotFoundException.class, () -> bookService.borrowBook(request));

		verify(bookRepository, times(1)).findByIdAndAvailable(bookId, true);
		verify(borrowerRepository, times(1)).findById(borrowerId);
		verify(bookRepository, never()).save(any());
	}

	@Test
	void returnBook_shouldSuccessfullyReturnBorrowedBook() {
		// Given
		Long bookId = 1L;
		Book borrowedBook = new Book();
		borrowedBook.setId(bookId);
		borrowedBook.setAvailable(false);
		Borrower borrower = new Borrower();
		borrower.setId(1L);
		borrowedBook.setBorrower(borrower);

		when(bookRepository.findById(bookId)).thenReturn(Optional.of(borrowedBook));
		when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// When
		Book result = bookService.returnBook(bookId);

		// Then
		assertNotNull(result);
		assertTrue(result.isAvailable());
		assertNull(result.getBorrower());
		verify(bookRepository, times(1)).findById(bookId);
		verify(bookRepository, times(1)).save(borrowedBook);
	}

	@Test
	void returnBook_shouldFailWhenBookNotFound() {
		// Given
		Long bookId = 1L;

		when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

		// When & Then
		assertThrows(ResourceNotFoundException.class, () -> bookService.returnBook(bookId));

		verify(bookRepository, times(1)).findById(bookId);
		verify(bookRepository, never()).save(any());
	}

	@Test
	void returnBook_shouldFailWhenBookNotBorrowed() {
		// Given
		Long bookId = 1L;
		Book availableBook = new Book();
		availableBook.setId(bookId);
		availableBook.setAvailable(true);

		when(bookRepository.findById(bookId)).thenReturn(Optional.of(availableBook));

		// When & Then
		assertThrows(IllegalStateException.class, () -> bookService.returnBook(bookId));

		verify(bookRepository, times(1)).findById(bookId);
		verify(bookRepository, never()).save(any());
	}

	@Test
	void getAllBooks_shouldReturnAllBooks() {
		// Given
		Book book1 = new Book();
		book1.setId(1L);
		Book book2 = new Book();
		book2.setId(2L);

		when(bookRepository.findAll()).thenReturn(List.of(book1, book2));

		// When
		List<Book> result = bookService.getAllBooks();

		// Then
		assertEquals(2, result.size());
		verify(bookRepository, times(1)).findAll();
	}

	@Test
	void getAvailableBooks_shouldReturnOnlyAvailableBooks() {
		// Given
		Book availableBook = new Book();
		availableBook.setId(1L);
		availableBook.setAvailable(true);
		Book borrowedBook = new Book();
		borrowedBook.setId(2L);
		borrowedBook.setAvailable(false);

		when(bookRepository.findByAvailable(true)).thenReturn(List.of(availableBook));

		// When
		List<Book> result = bookService.getAvailableBooks();

		// Then
		assertEquals(1, result.size());
		assertTrue(result.get(0).isAvailable());
		verify(bookRepository, times(1)).findByAvailable(true);
	}
}