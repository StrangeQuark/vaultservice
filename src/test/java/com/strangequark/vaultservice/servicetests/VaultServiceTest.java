package com.strangequark.vaultservice.servicetests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

public class VaultServiceTest extends BaseServiceTest {
    @Test
    void createServiceTest() {
        ResponseEntity<?> response =  vaultService.createService("testService1");

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertTrue(serviceRepository.findByName("testService1").isPresent());
    }

    @Test
    void createEnvironmentTest() {
        ResponseEntity<?> response =  vaultService.createEnvironment("testService", "testEnvironment1");

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertTrue(environmentRepository.findByNameAndServiceId("testEnvironment1", testService.getId()).isPresent());
    }

    @Test
    void getServiceTest() {
        ResponseEntity<?> response =  vaultService.getService("testService");

        Assertions.assertEquals(200, response.getStatusCode().value());
    }
}
