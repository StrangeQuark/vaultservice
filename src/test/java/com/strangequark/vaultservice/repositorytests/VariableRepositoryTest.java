package com.strangequark.vaultservice.repositorytests;

import com.strangequark.vaultservice.variable.Variable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

public class VariableRepositoryTest extends BaseRepositoryTest {

    @Test
    void findByEnvironmentIdTest() {
        List<Variable> response  = variableRepository.findByEnvironmentId(testEnvironment.getId());

        Assertions.assertTrue(response.contains(testVariable));
    }

    @Test
    void findByEnvironmentServiceIdTest() {
        List<Variable> response  = variableRepository.findByEnvironmentServiceId(testService.getId());

        Assertions.assertTrue(response.contains(testVariable));
    }

    @Test
    void findByEnvironmentIdAndKeyTest() {
        Optional<Variable> response  = variableRepository.findByEnvironmentIdAndKey(testEnvironment.getId(), testVariable.getKey());

        Assertions.assertTrue(response.isPresent());
    }
}
