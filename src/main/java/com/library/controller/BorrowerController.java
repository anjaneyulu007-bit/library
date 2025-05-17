package com.library.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.library.dto.BorrowerDTO;
import com.library.model.Borrower;
import com.library.service.BorrowerService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/borrowers")
@RequiredArgsConstructor
public class BorrowerController {
	@Autowired
	private BorrowerService borrowerService;

	@PostMapping
	public ResponseEntity<Borrower> registerBorrower(@Valid @RequestBody BorrowerDTO borrowerDTO) {
		Borrower borrower = borrowerService.registerBorrower(borrowerDTO);
		return ResponseEntity.created(URI.create("/api/borrowers/" + borrower.getId())).body(borrower);
	}
}
