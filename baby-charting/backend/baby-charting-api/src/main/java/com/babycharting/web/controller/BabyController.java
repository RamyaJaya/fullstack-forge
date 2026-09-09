package com.babycharting.web.controller;

import java.net.URI;

import com.babycharting.service.BabyService;
import com.babycharting.web.dto.BabyResponse;
import com.babycharting.web.dto.CreateBabyRequest;
import com.babycharting.web.dto.PagedResponse;
import com.babycharting.web.dto.UpdateBabyRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/babies")
@Tag(name = "Babies", description = "Create, read, update, and delete baby profiles")
public class BabyController {

	private final BabyService babyService;

	public BabyController(BabyService babyService) {
		this.babyService = babyService;
	}

	@PostMapping
	@Operation(summary = "Create a baby")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Baby created"),
			@ApiResponse(responseCode = "400", description = "Validation failed")
	})
	public ResponseEntity<BabyResponse> create(@Valid @RequestBody CreateBabyRequest request) {
		BabyResponse response = babyService.create(request);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(response.id())
				.toUri();
		return ResponseEntity.created(location).body(response);
	}

	@GetMapping
	@Operation(summary = "List babies", description = "Returns a paginated list of babies. Supports page, size, and sort query parameters.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Paginated list returned"),
			@ApiResponse(responseCode = "400", description = "Invalid pagination or sort parameters")
	})
	public PagedResponse<BabyResponse> findAll(
			@ParameterObject
			@PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
			Pageable pageable) {
		return babyService.findAll(pageable);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get a baby by id")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Baby found"),
			@ApiResponse(responseCode = "404", description = "Baby not found")
	})
	public BabyResponse findById(@PathVariable Long id) {
		return babyService.findById(id);
	}

	@PatchMapping("/{id}")
	@Operation(summary = "Partially update a baby", description = "Only provided fields are updated. Null fields are left unchanged.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Baby updated"),
			@ApiResponse(responseCode = "400", description = "Validation failed or empty update body"),
			@ApiResponse(responseCode = "404", description = "Baby not found")
	})
	public BabyResponse update(@PathVariable Long id, @Valid @RequestBody UpdateBabyRequest request) {
		return babyService.update(id, request);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete a baby")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Baby deleted"),
			@ApiResponse(responseCode = "404", description = "Baby not found")
	})
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		babyService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
