package com.strangequark.vaultservice.vault;

import com.strangequark.vaultservice.environment.Environment;
import com.strangequark.vaultservice.service.Service;
import com.strangequark.vaultservice.variable.Variable;
import com.strangequark.vaultservice.environment.EnvironmentRepository;
import com.strangequark.vaultservice.service.ServiceRepository;
import com.strangequark.vaultservice.variable.VariableRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@org.springframework.stereotype.Service
public class VaultService {

    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private EnvironmentRepository environmentRepository;
    @Autowired
    private VariableRepository variableRepository;

    public Service createService(String serviceName) {
        Service service = new Service();
        service.setName(serviceName);
        Service savedService = serviceRepository.save(service);

        // Add default environments
        createEnvironment(serviceName, "e1");
        createEnvironment(serviceName, "e2");
        createEnvironment(serviceName, "e3");

        return savedService;
    }

    public Environment createEnvironment(String serviceName, String environmentName) {
        Service service = serviceRepository.findByName(serviceName);
        if (service == null) throw new RuntimeException("Service not found");

        Environment environment = new Environment();
        environment.setName(environmentName);
        environment.setService(service);
        return environmentRepository.save(environment);
    }

    public List<Variable> getVariablesByService(String serviceName) {
        Service service = serviceRepository.findByName(serviceName);
        if (service == null) throw new RuntimeException("Service not found");
        return variableRepository.findByEnvironmentServiceId(service.getId());
    }

    public List<Variable> getVariablesByEnvironment(String serviceName, String environmentName) {
        Service service = serviceRepository.findByName(serviceName);
        if (service == null) throw new RuntimeException("Service not found");

        Environment environment = environmentRepository.findByNameAndServiceId(environmentName, service.getId());
        if (environment == null) throw new RuntimeException("Environment not found");

        return variableRepository.findByEnvironmentId(environment.getId());
    }

    public Variable getVariableByName(String serviceName, String environmentName, String variableName) {
        Service service = serviceRepository.findByName(serviceName);
        if (service == null) throw new RuntimeException("Service not found");

        Environment environment = environmentRepository.findByNameAndServiceId(environmentName, service.getId());
        if (environment == null) throw new RuntimeException("Environment not found");

        return variableRepository.findByEnvironmentIdAndKey(environment.getId(), variableName)
                .orElseThrow(() -> new RuntimeException("Variable not found"));
    }

    public Variable getVariable(Long id) {
        return variableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Variable not found"));
    }

    public Variable addVariable(String serviceName, String environmentName, Variable variable) {
        Service service = serviceRepository.findByName(serviceName);
        if (service == null) throw new RuntimeException("Service not found");

        Environment environment = environmentRepository.findByNameAndServiceId(environmentName, service.getId());
        if (environment == null) throw new RuntimeException("Environment not found");

        variable.setEnvironment(environment);
        return variableRepository.save(variable);
    }

    public void deleteVariable(Long id) {
        variableRepository.deleteById(id);
    }

    public void deleteEnvironment(String serviceName, String environmentName) {
        Service service = serviceRepository.findByName(serviceName);
        if (service == null) throw new RuntimeException("Service not found");

        Environment environment = environmentRepository.findByNameAndServiceId(environmentName, service.getId());
        if (environment == null) throw new RuntimeException("Environment not found");

        environmentRepository.delete(environment);
    }

    public void deleteService(String serviceName) {
        Service service = serviceRepository.findByName(serviceName);
        if (service == null) throw new RuntimeException("Service not found");

        serviceRepository.delete(service);
    }
}
