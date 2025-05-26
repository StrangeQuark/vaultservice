package com.strangequark.vaultservice.repositorytests;

import com.strangequark.vaultservice.environment.Environment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class EnvironmentRepositoryTest extends BaseRepositoryTest {
    @Test
    void findByNameAndServiceIdTest() {
        Optional<Environment> response = environmentRepository.findByNameAndServiceId(testEnvironment.getName(), testService.getId());

        Assertions.assertTrue(response.isPresent());
    }
}
