package com.library.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.library.dto.BorrowerDTO;
import com.library.exception.DuplicateResourceException;
import com.library.exception.ResourceNotFoundException;
import com.library.model.Borrower;
import com.library.repository.BorrowerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BorrowerService {

	private final BorrowerRepository borrowerRepository = null;

	public Borrower registerBorrower(BorrowerDTO borrowerDTO) {
		if (borrowerRepository.existsByEmail(borrowerDTO.getEmail())) {
			throw new DuplicateResourceException("Borrower with email " + borrowerDTO.getEmail() + " already exists");
		}

		Borrower borrower = new Borrower();
		borrower.setName(borrowerDTO.getName());
		borrower.setEmail(borrowerDTO.getEmail());
		return borrowerRepository.save(borrower);
	}

	public Borrower getBorrowerById(Long id) {
		return borrowerRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Borrower with ID " + id + " not found"));
	}

	public List<Borrower> getAllBorrowers() {
		return borrowerRepository.findAll();
	}
}
