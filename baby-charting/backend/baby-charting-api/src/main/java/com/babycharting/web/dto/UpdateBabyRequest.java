package com.babycharting.web.dto;

import java.time.LocalDate;

import com.babycharting.domain.Sex;

import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

public record UpdateBabyRequest(
		@Size(max = 100) String firstName,
		@Size(max = 100) String lastName,
		@PastOrPresent LocalDate dateOfBirth,
		Sex sex) {
}
