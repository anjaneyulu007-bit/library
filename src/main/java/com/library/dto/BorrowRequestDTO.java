package com.library.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowRequestDTO {

	@NotNull(message = "Book ID is required")
	private Long bookId;

	@NotNull(message = "Borrower ID is required")
	private Long borrowerId;

}
