package com.strangequark.vaultservice.servicetests;

import com.strangequark.vaultservice.serviceuser.ServiceUser;// Integration line: Auth
import com.strangequark.vaultservice.serviceuser.ServiceUserRequest;// Integration line: Auth
import com.strangequark.vaultservice.serviceuser.ServiceUserRole;// Integration line: Auth
import com.strangequark.vaultservice.variable.Variable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays; // Integration line: Auth
import java.util.List;

public class VaultServiceTest extends BaseServiceTest {
    @Test
    void createServiceTest() {
        ResponseEntity<?> response = vaultService.createService("testService1");

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertTrue(serviceRepository.findByName("testService1").isPresent());
    }

    @Test
    void createEnvironmentTest() {
        ResponseEntity<?> response = vaultService.createEnvironment(testService.getName(), "testEnvironment1");

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertTrue(environmentRepository.findByNameAndServiceId("testEnvironment1", testService.getId()).isPresent());
    }

    @Test
    void getServiceTest() {
        ResponseEntity<?> response = vaultService.getService(testService.getName());

        Assertions.assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void getEnvironmentsByServiceTest() {
        ResponseEntity<?> response = vaultService.getEnvironmentsByService(testService.getName());
        List<String> environments = (List<String>) response.getBody();

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertTrue(environments.contains(testEnvironment.getName()));
    }

    @Test
    void getEnvironmentTest() {
        ResponseEntity<?> response = vaultService.getEnvironment(testService.getName(), testEnvironment.getName());

        Assertions.assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void getVariablesByServiceTest() {
        ResponseEntity<?> response = vaultService.getVariablesByService(testService.getName());

        Assertions.assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void getVariablesByEnvironmentTest() {
        ResponseEntity<?> response = vaultService.getVariablesByEnvironment(testService.getName(), testEnvironment.getName());

        Assertions.assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void getVariableByNameTest() {
        ResponseEntity<?> response = vaultService.getVariableByName(testService.getName(), testEnvironment.getName(), "testKey");

        Assertions.assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void addVariableTest() {
        Variable variable = new Variable(testEnvironment, "testKey1", "testValue1");
        ResponseEntity<?> response = vaultService.addVariable(testService.getName(), testEnvironment.getName(), variable);

        Assertions.assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void updateVariableTest() {
        ResponseEntity<?> response = vaultService.updateVariable(testService.getName(), testEnvironment.getName(),
                new Variable(testEnvironment, testVariable.getKey(), "newValue"));

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertEquals("newValue", variableRepository.findByEnvironmentIdAndKey(testEnvironment.getId(), testVariable.getKey()).get().getValue());
    }

    @Test
    void updateVariablesTest() {
        List<Variable> vars = new ArrayList<>();
        vars.add(new Variable(testEnvironment, testVariable.getKey(), "newValue"));
        vars.add(new Variable(testEnvironment, "skippedKey", "skippedValue"));

        ResponseEntity<?> response = vaultService.updateVariables(testService.getName(), testEnvironment.getName(), vars);

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertEquals("Skipped variables: [skippedKey]", response.getBody().toString());
        Assertions.assertEquals("newValue", variableRepository.findByEnvironmentIdAndKey(testEnvironment.getId(), testVariable.getKey()).get().getValue());
    }

    @Test
    void addEnvFileTest() {
        String fileContent = "FOO=bar\nTEST=val1\n";

        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "test.env", "text/plain", fileContent.getBytes(StandardCharsets.UTF_8)
        );

        ResponseEntity<?> response = vaultService.addEnvFile(testService.getName(), testEnvironment.getName(), mockFile);

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Variables added: 2"));
    }

    @Test
    void downloadEnvFileTest() {
        vaultService.addVariable(testService.getName(), testEnvironment.getName(), new Variable(testEnvironment, "FOO", "bar"));
        vaultService.addVariable(testService.getName(), testEnvironment.getName(), new Variable(testEnvironment, "TEST", "val1"));

        ResponseEntity<?> response = vaultService.downloadEnvFile(testService.getName(), testEnvironment.getName());

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertTrue(response.getHeaders().getContentDisposition().getFilename().endsWith(".env"));

        ByteArrayResource body = (ByteArrayResource) response.getBody();
        String content = new String(body.getByteArray(), StandardCharsets.UTF_8);

        Assertions.assertTrue(content.contains("testKey=testValue"));
        Assertions.assertTrue(content.contains("FOO=bar"));
        Assertions.assertTrue(content.contains("TEST=val1"));
    }

    @Test
    void deleteVariableTest() {
        ResponseEntity<?> response = vaultService.deleteVariable(testService.getName(), testEnvironment.getName(), "testKey");

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertTrue(variableRepository.findByEnvironmentIdAndKey(testEnvironment.getId(), testVariable.getKey()).isEmpty());
    }

    @Test
    void deleteEnvironmentTest() {
        ResponseEntity<?> response = vaultService.deleteEnvironment(testService.getName(), testEnvironment.getName());

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertTrue(environmentRepository.findByNameAndServiceId(testEnvironment.getName(), testService.getId()).isEmpty());
    }

    @Test
    void deleteServiceTest() {
        ResponseEntity<?> response = vaultService.deleteService(testService.getName());

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertTrue(serviceRepository.findByName(testService.getName()).isEmpty());
    }
    // Integration function start: Auth
    @Test
    void getAllServicesTest() {
        ResponseEntity<?> response = vaultService.getAllServices();

        List<String> services = (List<String>) response.getBody();

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertTrue(services.contains(testService.getName()));
    }

    @Test
    void getUsersByServiceTest() {
        ResponseEntity<?> response = vaultService.getUsersByService(testService.getName());

        List<ServiceUser> users = (List<ServiceUser>) response.getBody();

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertTrue(users.stream().anyMatch(u -> u.getId().equals(serviceUser.getId())));
    }

    @Test
    void getAllRolesTest() {
        ResponseEntity<?> response = vaultService.getAllRoles();
        List<ServiceUserRole> roles = Arrays.asList(((ServiceUserRole[]) response.getBody()));

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertTrue(roles.contains(ServiceUserRole.OWNER));
        Assertions.assertTrue(roles.contains(ServiceUserRole.MANAGER));
        Assertions.assertTrue(roles.contains(ServiceUserRole.MAINTAINER));
    }

    @Test
    void getCurrentUserRoleTest() {
        ResponseEntity<?> response = vaultService.getCurrentUserRole(testService.getName());

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertEquals("OWNER", response.getBody().toString());
    }

    @Test
    void updateUserRoleTest() {
        serviceUserRepository.save(new ServiceUser(testService, testUserId, ServiceUserRole.MAINTAINER));

        Assertions.assertTrue(serviceUserRepository.findByUserIdAndServiceId(testUserId, testService.getId()).isPresent());

        ServiceUserRequest serviceUserRequest = new ServiceUserRequest();
        serviceUserRequest.setServiceName(testService.getName());
        serviceUserRequest.setUsername("testUser");
        serviceUserRequest.setRole(ServiceUserRole.OWNER);

        ResponseEntity<?> response = vaultService.updateUserRole(serviceUserRequest);

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertEquals(ServiceUserRole.OWNER, serviceUserRepository.findByUserIdAndServiceId(testUserId, testService.getId()).get().getRole());
    }

    @Test
    void addUserToServiceTest() {
        ServiceUserRequest serviceUserRequest = new ServiceUserRequest();
        serviceUserRequest.setServiceName(testService.getName());
        serviceUserRequest.setUsername("testUser");

        ResponseEntity<?> response = vaultService.addUserToService(serviceUserRequest);

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertTrue(serviceUserRepository.findByUserIdAndServiceId(testUserId, testService.getId()).isPresent());
    }

    @Test
    void deleteUserFromServiceTest() {
        serviceUserRepository.save(new ServiceUser(testService, testUserId, ServiceUserRole.MAINTAINER));

        Assertions.assertTrue(serviceUserRepository.findByUserIdAndServiceId(testUserId, testService.getId()).isPresent());

        ServiceUserRequest serviceUserRequest = new ServiceUserRequest();
        serviceUserRequest.setServiceName(testService.getName());
        serviceUserRequest.setUsername("testUser");

        ResponseEntity<?> response = vaultService.deleteUserFromService(serviceUserRequest);

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertTrue(serviceUserRepository.findByUserIdAndServiceId(testUserId, testService.getId()).isEmpty());
    }

    @Test
    void deleteUserFromAllServicesTest() {
        serviceUserRepository.save(new ServiceUser(testService, testUserId, ServiceUserRole.MAINTAINER));

        Assertions.assertTrue(serviceUserRepository.findByUserIdAndServiceId(testUserId, testService.getId()).isPresent());

        ServiceUserRequest serviceUserRequest = new ServiceUserRequest();
        serviceUserRequest.setServiceName(testService.getName());
        serviceUserRequest.setUsername("testUser");

        ResponseEntity<?> response = vaultService.deleteUserFromAllServices(serviceUserRequest);

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertTrue(serviceUserRepository.findByUserIdAndServiceId(testUserId, testService.getId()).isEmpty());
    }// Integration function end: Auth
}
