package com.strangequark.vaultservice.servicetests;

import com.strangequark.vaultservice.service.Service;
import com.strangequark.vaultservice.service.ServiceRepository;
import com.strangequark.vaultservice.serviceuser.ServiceUserRepository;
import com.strangequark.vaultservice.vault.VaultService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
        "authservice.integration=false",
        "telemetryservice.integration=false",
        "SERVICE_SECRET_VAULT=test"
})
@ActiveProfiles("test")
public class VaultServiceIntegrationTest {
    static {
        System.setProperty("ENCRYPTION_KEY", "AA1A2A8C0E4F76FB3C13F66225AAAC42");
    }

    @Autowired
    private VaultService vaultService;
    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private ServiceUserRepository serviceUserRepository;

    @AfterEach
    void teardown() {
        serviceRepository.deleteAll();
    }

    @Test
    void createServiceWithoutAuthTest() {
        ResponseEntity<?> response = vaultService.createService("testService");

        Assertions.assertEquals(200, response.getStatusCode().value());

        Service service = serviceRepository.findByName("testService").get();
        Assertions.assertTrue(serviceUserRepository.findAllByServiceId(service.getId()).isEmpty());
    }

    @Test
    void getAllRolesWithoutAuthTest() {
        ResponseEntity<?> response = vaultService.getAllRoles();

        Assertions.assertEquals(404, response.getStatusCode().value());
    }
}
