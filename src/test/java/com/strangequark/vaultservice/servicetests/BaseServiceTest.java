package com.strangequark.vaultservice.servicetests;

import com.strangequark.vaultservice.environment.Environment;
import com.strangequark.vaultservice.environment.EnvironmentRepository;
import com.strangequark.vaultservice.service.Service;
import com.strangequark.vaultservice.service.ServiceRepository;
import com.strangequark.vaultservice.utility.EncryptionService;
import com.strangequark.vaultservice.variable.Variable;
import com.strangequark.vaultservice.variable.VariableRepository;
import com.strangequark.vaultservice.vault.VaultService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public abstract class BaseServiceTest {

    @Autowired
    public ServiceRepository serviceRepository;
    @Autowired
    public EnvironmentRepository environmentRepository;
    @Autowired
    public VariableRepository variableRepository;
    @Autowired
    public VaultService vaultService;
    @Autowired
    public EncryptionService encryptionService;

    public Service testService;
    public Environment testEnvironment;
    public Variable testVariable;

    @BeforeEach
    void setup() {
        try {
            testService = new Service("testService");
            testEnvironment = new Environment(testService, "testEnvironment");
            testVariable = new Variable(testEnvironment, encryptionService.encrypt("testKey"), encryptionService.encrypt("testValue"));

            serviceRepository.save(testService);
            environmentRepository.save(testEnvironment);
            variableRepository.save(testVariable);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @AfterEach
    void teardown() {
        variableRepository.deleteAll();
        environmentRepository.deleteAll();
        serviceRepository.deleteAll();
    }
}
