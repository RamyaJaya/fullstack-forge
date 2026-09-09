package com.babycharting.web.dto;

import java.time.Instant;
import java.time.LocalDate;

import com.babycharting.domain.Sex;

public record BabyResponse(
		Long id,
		String firstName,
		String lastName,
		LocalDate dateOfBirth,
		Sex sex,
		Instant createdAt,
		Instant updatedAt) {
}
