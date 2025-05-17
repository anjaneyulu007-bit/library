package com.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {

	@NotBlank(message = "ISBN is required")
	@Pattern(regexp = "^(?:ISBN(?:-13)?:? )?(?=[0-9]{13}$|\\d{10}$)", message = "Invalid ISBN format")
	private String isbn;

	@NotBlank(message = "Title is required")
	private String title;

	@NotBlank(message = "Author is required")
	private String author;

}
