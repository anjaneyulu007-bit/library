package com.library.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.library.model.Borrower;

public interface BorrowerRepository extends JpaRepository<Borrower, Long> {
	boolean existsByEmail(String email);

	Optional<Borrower> findByEmail(String email);
}
