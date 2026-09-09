package com.babycharting.web.dto;

import java.time.LocalDate;

import com.babycharting.domain.Sex;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

public record CreateBabyRequest(
		@NotBlank @Size(max = 100) String firstName,
		@NotBlank @Size(max = 100) String lastName,
		@NotNull @PastOrPresent LocalDate dateOfBirth,
		Sex sex) {
}
