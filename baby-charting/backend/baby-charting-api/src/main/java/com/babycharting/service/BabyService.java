package com.babycharting.service;

import java.util.Set;

import com.babycharting.domain.Baby;
import com.babycharting.exception.BadRequestException;
import com.babycharting.repository.BabyRepository;
import com.babycharting.web.dto.BabyResponse;
import com.babycharting.web.dto.CreateBabyRequest;
import com.babycharting.web.dto.PagedResponse;
import com.babycharting.web.dto.UpdateBabyRequest;
import com.babycharting.web.mapper.BabyMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BabyService {

	private static final Set<String> ALLOWED_SORT_PROPERTIES = Set.of(
			"createdAt", "firstName", "lastName", "dateOfBirth", "id");

	private final BabyRepository babyRepository;
	private final BabyAccessService babyAccessService;
	private final BabyMapper babyMapper;

	public BabyService(
			BabyRepository babyRepository,
			BabyAccessService babyAccessService,
			BabyMapper babyMapper) {
		this.babyRepository = babyRepository;
		this.babyAccessService = babyAccessService;
		this.babyMapper = babyMapper;
	}

	@Transactional
	public BabyResponse create(CreateBabyRequest request) {
		Baby baby = babyMapper.toEntity(request);
		Baby savedBaby = babyRepository.save(baby);
		return babyMapper.toResponse(savedBaby);
	}

	@Transactional(readOnly = true)
	public PagedResponse<BabyResponse> findAll(Pageable pageable) {
		validateSortProperties(pageable);

		Page<Baby> page = babyRepository.findAll(pageable);
		return babyMapper.toPagedResponse(page);
	}

	@Transactional(readOnly = true)
	public BabyResponse findById(Long id) {
		Baby baby = babyAccessService.requireBabyExists(id);
		return babyMapper.toResponse(baby);
	}

	@Transactional
	public BabyResponse update(Long id, UpdateBabyRequest request) {
		validateUpdateRequest(request);

		Baby baby = babyAccessService.requireBabyExists(id);
		babyMapper.applyUpdate(baby, request);
		Baby savedBaby = babyRepository.save(baby);
		return babyMapper.toResponse(savedBaby);
	}

	@Transactional
	public void delete(Long id) {
		Baby baby = babyAccessService.requireBabyExists(id);
		babyRepository.delete(baby);
	}

	private void validateUpdateRequest(UpdateBabyRequest request) {
		if (request.firstName() == null
				&& request.lastName() == null
				&& request.dateOfBirth() == null
				&& request.sex() == null) {
			throw new BadRequestException("At least one field must be provided");
		}

		if (request.firstName() != null && request.firstName().isBlank()) {
			throw new BadRequestException("firstName must not be blank");
		}

		if (request.lastName() != null && request.lastName().isBlank()) {
			throw new BadRequestException("lastName must not be blank");
		}
	}

	private void validateSortProperties(Pageable pageable) {
		for (Sort.Order order : pageable.getSort()) {
			if (!ALLOWED_SORT_PROPERTIES.contains(order.getProperty())) {
				throw new BadRequestException("Invalid sort property: " + order.getProperty());
			}
		}
	}

}
