package com.babycharting.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
		info = @Info(
				title = "Baby Charting API",
				version = "v1",
				description = """
						Baby charting REST API for managing baby profiles.

						No authentication is required in the current phase. \
						Use this API for local development only.
						"""))
public class OpenApiConfig {
}
