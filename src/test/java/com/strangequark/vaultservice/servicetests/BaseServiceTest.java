package com.strangequark.vaultservice.servicetests;

import com.strangequark.vaultservice.environment.Environment;
import com.strangequark.vaultservice.environment.EnvironmentRepository;
import com.strangequark.vaultservice.service.Service;
import com.strangequark.vaultservice.service.ServiceRepository;
import com.strangequark.vaultservice.serviceuser.ServiceUser;
import com.strangequark.vaultservice.serviceuser.ServiceUserRepository;
import com.strangequark.vaultservice.serviceuser.ServiceUserRole;
import com.strangequark.vaultservice.utility.AuthUtility;
import com.strangequark.vaultservice.utility.JwtUtility;
import com.strangequark.vaultservice.variable.Variable;
import com.strangequark.vaultservice.variable.VariableRepository;
import com.strangequark.vaultservice.vault.VaultService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import java.util.UUID;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public abstract class BaseServiceTest {
    static {
        System.setProperty("ENCRYPTION_KEY", "AA1A2A8C0E4F76FB3C13F66225AAAC42");
    }

    @Autowired
    public ServiceRepository serviceRepository;
    @Autowired
    public EnvironmentRepository environmentRepository;
    @Autowired
    public VariableRepository variableRepository;
    @Autowired
    public VaultService vaultService;

    public Service testService;
    public Environment testEnvironment;
    public Variable testVariable;
    @Value("${BOOTSTRAP_TOKEN}")
    public String BOOTSTRAP_TOKEN;

    @Value("${AUTH_CICD_TOKEN}")
    public String AUTH_CICD_TOKEN;
    @Autowired
    public ServiceUserRepository serviceUserRepository;
    @MockitoBean
    public JwtUtility jwtUtility;
    @MockitoBean
    public AuthUtility authUtility;
    public UUID testOwnerId = UUID.randomUUID();
    public UUID testUserId = UUID.randomUUID();
    public ServiceUser serviceUser;
    public String testBootstrapService = "testBootstrapService";


    @BeforeEach
    void setup() {
        try {
            testService = new Service("testService");
            testEnvironment = new Environment(testService, "testEnvironment");
            testVariable = new Variable(testEnvironment, "testKey", "testValue");

            serviceRepository.save(testService);
            environmentRepository.save(testEnvironment);
            variableRepository.save(testVariable);

            serviceUser = new ServiceUser(testService, testOwnerId, ServiceUserRole.OWNER);
            serviceUserRepository.save(serviceUser);

            // Mock authUtility functions
            when(jwtUtility.extractId()).thenReturn(testOwnerId.toString());
            when(authUtility.getUserId("testUser")).thenReturn(testUserId.toString());
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
