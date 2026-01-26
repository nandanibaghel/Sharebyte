package com.sharebyte.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sharebyte.entities.VerificationToken;

public interface VerificationRepo extends JpaRepository<VerificationToken, Long> {
	Optional<VerificationToken> findByToken(String token);

}