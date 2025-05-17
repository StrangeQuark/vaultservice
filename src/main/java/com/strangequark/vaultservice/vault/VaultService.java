package com.strangequark.vaultservice.vault;

import com.strangequark.vaultservice.environment.Environment;
import com.strangequark.vaultservice.error.ErrorResponse;
import com.strangequark.vaultservice.service.Service;
import com.strangequark.vaultservice.variable.Variable;
import com.strangequark.vaultservice.environment.EnvironmentRepository;
import com.strangequark.vaultservice.service.ServiceRepository;
import com.strangequark.vaultservice.variable.VariableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.List;

@org.springframework.stereotype.Service
public class VaultService {

    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private EnvironmentRepository environmentRepository;
    @Autowired
    private VariableRepository variableRepository;

    public ResponseEntity<?> createService(String serviceName) {
        try {
            Service service = new Service();
            service.setName(serviceName);
            Service savedService = serviceRepository.save(service);

            // Add default environments
            createEnvironment(serviceName, "e1");
            createEnvironment(serviceName, "e2");
            createEnvironment(serviceName, "e3");

            return ResponseEntity.ok(savedService);
        } catch (Exception ex) {
            return ResponseEntity.status(400).body(new ErrorResponse("Service creation failed"));
        }
    }

    public ResponseEntity<?> createEnvironment(String serviceName, String environmentName) {
        try {
            Service service = serviceRepository.findServiceByName(serviceName);
            if (service == null) throw new RuntimeException("Service not found");

            Environment environment = new Environment();
            environment.setName(environmentName);
            environment.setService(service);
            environmentRepository.save(environment);

            return ResponseEntity.ok(environment);
        } catch (RuntimeException runtimeException) {
            return ResponseEntity.status(400).body(new ErrorResponse(runtimeException.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(400).body(new ErrorResponse("Environment creation failed"));
        }
    }

    public ResponseEntity<?> getVariablesByService(String serviceName) {
        try {
            Service service = serviceRepository.findServiceByName(serviceName);
            if (service == null) throw new RuntimeException("Service not found");
            List<Variable> variables = variableRepository.findVariablesByServiceId(service.getId());

            return ResponseEntity.ok(variables);
        } catch (RuntimeException runtimeException) {
            return ResponseEntity.status(400).body(new ErrorResponse(runtimeException.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(400).body(new ErrorResponse("Unable to fetch variables for " + serviceName));
        }

    }

    public ResponseEntity<?> getVariablesByEnvironment(String serviceName, String environmentName) {
        try {
            Service service = serviceRepository.findServiceByName(serviceName);
            if (service == null) throw new RuntimeException("Service not found");

            Environment environment = environmentRepository.findEnvironmentByNameAndServiceId(environmentName, service.getId());
            if (environment == null) throw new RuntimeException("Environment not found");

            List<Variable> variables = variableRepository.findVariablesByEnvironmentId(environment.getId());

            return ResponseEntity.ok(variables);
        } catch (RuntimeException runtimeException) {
            return ResponseEntity.status(400).body(new ErrorResponse(runtimeException.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(400).body(new ErrorResponse("Unable to fetch variables for " +
                    environmentName + " of " + serviceName + " service"));
        }
    }

    public ResponseEntity<?> getVariableByName(String serviceName, String environmentName, String variableName) {
        try {
            Service service = serviceRepository.findServiceByName(serviceName);
            if (service == null) throw new RuntimeException("Service not found");

            Environment environment = environmentRepository.findEnvironmentByNameAndServiceId(environmentName, service.getId());
            if (environment == null) throw new RuntimeException("Environment not found");

            Variable variable = variableRepository.findVariableByEnvironmentIdAndKey(environment.getId(), variableName)
                    .orElseThrow(() -> new RuntimeException("Variable not found"));

            return ResponseEntity.ok(variable);
        } catch (RuntimeException runtimeException) {
            return ResponseEntity.status(400).body(new ErrorResponse(runtimeException.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(400).body(new ErrorResponse("Unable to fetch variable " + variableName));
        }
    }

    public ResponseEntity<?> addVariable(String serviceName, String environmentName, Variable variable) {
        try {
            Service service = serviceRepository.findServiceByName(serviceName);
            if (service == null) throw new RuntimeException("Service not found");

            Environment environment = environmentRepository.findEnvironmentByNameAndServiceId(environmentName, service.getId());
            if (environment == null) throw new RuntimeException("Environment not found");

            variable.setEnvironment(environment);
            variableRepository.save(variable);

            return ResponseEntity.ok("Variable successfully added");
        } catch (RuntimeException runtimeException) {
            return ResponseEntity.status(400).body(new ErrorResponse(runtimeException.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(400).body(new ErrorResponse("Unable to add variable"));
        }
    }

    public ResponseEntity<?> deleteVariable(Long id) {
        try {
            variableRepository.deleteById(id);

            return ResponseEntity.ok("Variable successfully deleted");
        } catch (Exception ex) {
            return ResponseEntity.status(400).body(new ErrorResponse("Unable to delete variable"));
        }
    }

    public ResponseEntity<?> deleteEnvironment(String serviceName, String environmentName) {
        try {
            Service service = serviceRepository.findServiceByName(serviceName);
            if (service == null) throw new RuntimeException("Service not found");

            Environment environment = environmentRepository.findEnvironmentByNameAndServiceId(environmentName, service.getId());
            if (environment == null) throw new RuntimeException("Environment not found");

            environmentRepository.delete(environment);

            return ResponseEntity.ok("Environment successfully deleted");
        } catch (RuntimeException runtimeException) {
            return ResponseEntity.status(400).body(new ErrorResponse(runtimeException.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(400).body(new ErrorResponse("Unable to delete environment"));
        }
    }

    public ResponseEntity<?> deleteService(String serviceName) {
        try {
            Service service = serviceRepository.findServiceByName(serviceName);
            if (service == null) throw new RuntimeException("Service not found");

            serviceRepository.delete(service);

            return ResponseEntity.ok("Service successfully deleted");
        } catch (RuntimeException runtimeException) {
            return ResponseEntity.status(400).body(new ErrorResponse(runtimeException.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(400).body(new ErrorResponse("Unable to delete service"));
        }
    }
}
