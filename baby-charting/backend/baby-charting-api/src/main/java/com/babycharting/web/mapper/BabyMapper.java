package com.babycharting.web.mapper;

import com.babycharting.domain.Baby;
import com.babycharting.web.dto.BabyResponse;
import com.babycharting.web.dto.CreateBabyRequest;
import com.babycharting.web.dto.PagedResponse;
import com.babycharting.web.dto.UpdateBabyRequest;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class BabyMapper {

	public Baby toEntity(CreateBabyRequest request) {
		Baby baby = new Baby();
		baby.setFirstName(request.firstName());
		baby.setLastName(request.lastName());
		baby.setDateOfBirth(request.dateOfBirth());
		baby.setSex(request.sex());
		return baby;
	}

	public void applyUpdate(Baby baby, UpdateBabyRequest request) {
		if (request.firstName() != null) {
			baby.setFirstName(request.firstName());
		}
		if (request.lastName() != null) {
			baby.setLastName(request.lastName());
		}
		if (request.dateOfBirth() != null) {
			baby.setDateOfBirth(request.dateOfBirth());
		}
		if (request.sex() != null) {
			baby.setSex(request.sex());
		}
	}

	public BabyResponse toResponse(Baby baby) {
		return new BabyResponse(
				baby.getId(),
				baby.getFirstName(),
				baby.getLastName(),
				baby.getDateOfBirth(),
				baby.getSex(),
				baby.getCreatedAt(),
				baby.getUpdatedAt());
	}

	public PagedResponse<BabyResponse> toPagedResponse(Page<Baby> page) {
		return PagedResponse.from(page.map(this::toResponse));
	}

}
