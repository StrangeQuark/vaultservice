// Integration file: Auth

package com.strangequark.vaultservice.repositorytests;

import com.strangequark.vaultservice.service.Service;
import com.strangequark.vaultservice.serviceuser.ServiceUser;
import com.strangequark.vaultservice.serviceuser.ServiceUserRepository;
import com.strangequark.vaultservice.serviceuser.ServiceUserRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

public class ServiceUserRepositoryTest extends BaseRepositoryTest {
    @Autowired
    private ServiceUserRepository serviceUserRepository;

    private UUID testUserId;
    private ServiceUser testUser;

    @BeforeEach
    void setup() {
        testUserId = UUID.randomUUID();
        testUser = new ServiceUser(testService, testUserId, ServiceUserRole.OWNER);

        testEntityManager.persistAndFlush(testUser);
    }

    @Test
    void findByUserIdAndServiceIdTest() {
        ServiceUser user = serviceUserRepository.findByUserIdAndServiceId(testUserId, testService.getId())
                .orElseThrow(() -> new AssertionError("User not found"));

        Assertions.assertEquals(ServiceUserRole.OWNER, user.getRole());
    }

    @Test
    void findServicesByUserIdTest() {
        List<Service> serviceList = serviceUserRepository.findServicesByUserId(testUserId);

        Assertions.assertEquals(1, serviceList.size());
        Assertions.assertTrue(serviceList.contains(testService));
    }

    @Test
    void findAllByServiceIdTest() {
        List<ServiceUser> serviceUserList = serviceUserRepository.findAllByServiceId(testService.getId());

        Assertions.assertEquals(1, serviceUserList.size());
        Assertions.assertTrue(serviceUserList.contains(testUser));
    }

    @Test
    void deleteServiceUserTest() {
        // First insert a ServiceUser to be deleted
        UUID userId = UUID.randomUUID();
        testUser = new ServiceUser(testService, userId, ServiceUserRole.MAINTAINER);
        testEntityManager.persistAndFlush(testUser);

        Assertions.assertTrue(serviceUserRepository.findByUserIdAndServiceId(userId, testService.getId()).isPresent());

        // Now attempt deletion
        int response = serviceUserRepository.deleteServiceUser(userId, testService.getId());

        Assertions.assertEquals(1, response);
        Assertions.assertFalse(serviceUserRepository.findByUserIdAndServiceId(userId, testService.getId()).isPresent());
    }
}
