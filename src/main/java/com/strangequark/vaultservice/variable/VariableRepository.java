package com.strangequark.vaultservice.variable;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VariableRepository extends JpaRepository<Variable, Long> {
    List<Variable> findVariablesByEnvironmentId(Long environmentId);
    List<Variable> findVariablesByServiceId(Long serviceId);
    Optional<Variable> findVariableByEnvironmentIdAndKey(Long environmentId, String key);
}
