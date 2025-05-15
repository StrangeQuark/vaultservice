package com.strangequark.vaultservice.variable;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VariableRepository extends JpaRepository<Variable, Long> {
    List<Variable> findByEnvironmentId(Long environmentId);
    List<Variable> findByEnvironmentServiceId(Long serviceId);
    Optional<Variable> findByEnvironmentIdAndKey(Long environmentId, String key);
}
