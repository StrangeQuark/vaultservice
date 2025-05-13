package com.strangequark.vaultservice.vault;

import com.strangequark.vaultservice.service.ServiceVariable;
import com.strangequark.vaultservice.service.ServiceVariableRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class VaultService {

    @Autowired
    private ServiceVariableRepository repository;

    public List<ServiceVariable> getVariablesByService(String serviceName) {
        return repository.findByServiceName(serviceName);
    }

    public List<ServiceVariable> getVariablesByEnvironment(String serviceName, String environment) {
        return repository.findByServiceNameAndEnvironment(serviceName, environment);
    }

    public ServiceVariable getVariable(String serviceName, String environment, String key) {
        return repository.findByServiceNameAndEnvironmentAndKey(serviceName, environment, key);
    }

    public ServiceVariable addVariable(ServiceVariable variable) {
        return repository.save(variable);
    }

    public void deleteVariable(Integer id) {
        repository.deleteById(id);
    }
}
