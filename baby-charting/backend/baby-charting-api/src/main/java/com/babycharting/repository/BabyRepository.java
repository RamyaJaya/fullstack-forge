package com.babycharting.repository;

import com.babycharting.domain.Baby;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BabyRepository extends JpaRepository<Baby, Long> {
}
