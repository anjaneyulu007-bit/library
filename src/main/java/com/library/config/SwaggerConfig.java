package com.library.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {
	@Bean
	public OpenAPI libraryOpenAPI() {
		return new OpenAPI().info(new Info().title("Library Management API")
				.description("API for managing library books and borrowers").version("v1.0"));
	}
}
