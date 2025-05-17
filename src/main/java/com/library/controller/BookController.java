package com.library.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.library.dto.BookDTO;
import com.library.dto.BorrowRequestDTO;
import com.library.model.Book;
import com.library.service.BookService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
	@Autowired
	private BookService bookService;

	@PostMapping
	public ResponseEntity<Book> registerBook(@Valid @RequestBody BookDTO bookDTO) {
		Book book = bookService.registerBook(bookDTO);
		return ResponseEntity.created(URI.create("/api/books/" + book.getId())).body(book);
	}

	@GetMapping
	public ResponseEntity<List<Book>> getAllBooks() {
		return ResponseEntity.ok(bookService.getAllBooks());
	}

	@PostMapping("/{bookId}/borrow")
	public ResponseEntity<Book> borrowBook(@PathVariable Long bookId, @RequestParam Long borrowerId) {
		BorrowRequestDTO request = new BorrowRequestDTO(bookId, borrowerId);
		return ResponseEntity.ok(bookService.borrowBook(request));
	}

	@PostMapping("/{bookId}/return")
	public ResponseEntity<Book> returnBook(@PathVariable Long bookId) {
		return ResponseEntity.ok(bookService.returnBook(bookId));
	}
}