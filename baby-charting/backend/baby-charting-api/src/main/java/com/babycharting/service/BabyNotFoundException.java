package com.babycharting.service;

public class BabyNotFoundException extends RuntimeException {

	public BabyNotFoundException(Long babyId) {
		super("Baby not found with id: " + babyId);
	}

}
