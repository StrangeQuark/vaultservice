package com.strangequark.vaultservice.environment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EnvironmentRepository extends JpaRepository<Environment, Long> {
    Optional<Environment> findByNameAndServiceId(String name, Long serviceId);
}
