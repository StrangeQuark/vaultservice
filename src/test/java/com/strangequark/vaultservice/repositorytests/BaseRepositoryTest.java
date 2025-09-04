package com.strangequark.vaultservice.repositorytests;

import com.strangequark.vaultservice.environment.Environment;
import com.strangequark.vaultservice.environment.EnvironmentRepository;
import com.strangequark.vaultservice.service.Service;
import com.strangequark.vaultservice.service.ServiceRepository;
import com.strangequark.vaultservice.variable.Variable;
import com.strangequark.vaultservice.variable.VariableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@EntityScan(basePackages = "com.strangequark.vaultservice")
public class BaseRepositoryTest {
    static {
        System.setProperty("ENCRYPTION_KEY", "AA1A2A8C0E4F76FB3C13F66225AAAC42");
    }

    @Autowired
    public TestEntityManager testEntityManager;

    @Autowired
    public ServiceRepository serviceRepository;
    @Autowired
    public EnvironmentRepository environmentRepository;
    @Autowired
    public VariableRepository variableRepository;

    public Service testService;
    public Environment testEnvironment;
    public Variable testVariable;

    @BeforeEach
    void beforeEach() {
        testService = new Service("testService");
        testEnvironment = new Environment(testService, "testEnvironment");
        testVariable = new Variable(testEnvironment, "testKey", "testValue");

        testEntityManager.persistAndFlush(testService);
        testEntityManager.persistAndFlush(testEnvironment);
        testEntityManager.persistAndFlush(testVariable);
    }
}
