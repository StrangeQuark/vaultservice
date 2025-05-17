package com.strangequark.vaultservice.environment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EnvironmentRepository extends JpaRepository<Environment, Long> {
    Environment findByNameAndServiceId(String name, Long serviceId);
}
