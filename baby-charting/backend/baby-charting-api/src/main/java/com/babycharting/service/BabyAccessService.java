package com.babycharting.service;

import com.babycharting.domain.Baby;
import com.babycharting.repository.BabyRepository;

import org.springframework.stereotype.Service;

@Service
public class BabyAccessService {

	private final BabyRepository babyRepository;

	public BabyAccessService(BabyRepository babyRepository) {
		this.babyRepository = babyRepository;
	}

	public Baby requireBabyExists(Long babyId) {
		return babyRepository.findById(babyId)
				.orElseThrow(() -> new BabyNotFoundException(babyId));
	}

}
