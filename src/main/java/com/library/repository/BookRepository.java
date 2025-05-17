package com.library.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.library.model.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
	List<Book> findByIsbn(String isbn);

	List<Book> findByAvailable(boolean available);

	Optional<Book> findByIdAndAvailable(Long id, boolean available);
}