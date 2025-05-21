package com.strangequark.vaultservice.vault;

import com.strangequark.vaultservice.environment.Environment;
import com.strangequark.vaultservice.error.ErrorResponse;
import com.strangequark.vaultservice.service.Service;
import com.strangequark.vaultservice.utility.EncryptionUtility;
import com.strangequark.vaultservice.variable.Variable;
import com.strangequark.vaultservice.environment.EnvironmentRepository;
import com.strangequark.vaultservice.service.ServiceRepository;
import com.strangequark.vaultservice.variable.VariableRepository;
import com.strangequark.vaultservice.variable.VariableResponse;
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
    @Autowired
    private EncryptionUtility encryptionUtility;

    public ResponseEntity<?> createService(String serviceName) {
        try {
            for(Service s : serviceRepository.findAll()) {
                if(s.getName().equals(serviceName))
                    return ResponseEntity.status(400).body(new ErrorResponse("That service name already exists"));
            }

            Service service = new Service();
            service.setName(serviceName);

            Service savedService = serviceRepository.save(service);

            return ResponseEntity.ok(savedService);
        } catch (Exception ex) {
            return ResponseEntity.status(400).body(new ErrorResponse("Service creation failed"));
        }
    }

    public ResponseEntity<?> createEnvironment(String serviceName, String environmentName) {
        try {
            Service service = serviceRepository.findByName(serviceName)
                    .orElseThrow(() -> new RuntimeException("Service not found"));

            for(Environment env : service.getEnvironments()) {
                if(env.getName().equals(environmentName))
                    return ResponseEntity.status(400).body(new ErrorResponse("Environment with that name already exists in this service"));
            }

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

    public ResponseEntity<?> getService(String serviceName) {
        try {
            Service service = serviceRepository.findByName(serviceName)
                    .orElseThrow(() -> new RuntimeException("Service not found"));

            for(Environment env : service.getEnvironments()) {
                env.setVariables(encryptionUtility.decryptList(env.getVariables()));
            }

            return ResponseEntity.ok(service);
        } catch (RuntimeException runtimeException) {
            return ResponseEntity.status(400).body(new ErrorResponse(runtimeException.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(400).body(new ErrorResponse("Unable to delete service"));
        }
    }

    public ResponseEntity<?> getEnvironment(String serviceName, String environmentName) {
        try {
            Service service = serviceRepository.findByName(serviceName)
                    .orElseThrow(() -> new RuntimeException("Service not found"));

            Environment environment = environmentRepository.findByNameAndServiceId(environmentName, service.getId())
                    .orElseThrow(() -> new RuntimeException("Environment not found"));

            environment.setVariables(encryptionUtility.decryptList(environment.getVariables()));

            return ResponseEntity.ok(environment);
        } catch (RuntimeException runtimeException) {
            return ResponseEntity.status(400).body(new ErrorResponse(runtimeException.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(400).body(new ErrorResponse("Unable to delete environment"));
        }
    }

    public ResponseEntity<?> getVariablesByService(String serviceName) {
        try {
            Service service = serviceRepository.findByName(serviceName)
                    .orElseThrow(() -> new RuntimeException("Service not found"));

            List<Variable> variables = variableRepository.findByEnvironmentServiceId(service.getId());

            return ResponseEntity.ok(encryptionUtility.decryptList(variables));
        } catch (RuntimeException runtimeException) {
            return ResponseEntity.status(400).body(new ErrorResponse(runtimeException.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(400).body(new ErrorResponse("Unable to fetch variables for " + serviceName));
        }

    }

    public ResponseEntity<?> getVariablesByEnvironment(String serviceName, String environmentName) {
        try {
            Service service = serviceRepository.findByName(serviceName)
                    .orElseThrow(() -> new RuntimeException("Service not found"));

            Environment environment = environmentRepository.findByNameAndServiceId(environmentName, service.getId())
                    .orElseThrow(() -> new RuntimeException("Environment not found"));

            List<Variable> variables = variableRepository.findByEnvironmentId(environment.getId());

            return ResponseEntity.ok(encryptionUtility.decryptList(variables));
        } catch (RuntimeException runtimeException) {
            return ResponseEntity.status(400).body(new ErrorResponse(runtimeException.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(400).body(new ErrorResponse("Unable to fetch variables for " +
                    environmentName + " of " + serviceName + " service"));
        }
    }

    public ResponseEntity<?> getVariableByName(String serviceName, String environmentName, String variableName) {
        try {
            Service service = serviceRepository.findByName(serviceName)
                    .orElseThrow(() -> new RuntimeException("Service not found"));

            Environment environment = environmentRepository.findByNameAndServiceId(environmentName, service.getId())
                    .orElseThrow(() -> new RuntimeException("Environment not found"));

            Variable variable = variableRepository.findByEnvironmentIdAndKey(environment.getId(), encryptionUtility.encrypt(variableName))
                    .orElseThrow(() -> new RuntimeException("Variable not found"));

            //Decrypt the variables key/value pair
            variable.setKey(encryptionUtility.decrypt(variable.getKey()));
            variable.setValue(encryptionUtility.decrypt(variable.getValue()));

            return ResponseEntity.ok(new VariableResponse(variable));
        } catch (RuntimeException runtimeException) {
            return ResponseEntity.status(400).body(new ErrorResponse(runtimeException.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(400).body(new ErrorResponse("Unable to fetch variable " + variableName));
        }
    }

    public ResponseEntity<?> addVariable(String serviceName, String environmentName, Variable variable) {
        try {
            Service service = serviceRepository.findByName(serviceName)
                    .orElseThrow(() -> new RuntimeException("Service not found"));

            Environment environment = environmentRepository.findByNameAndServiceId(environmentName, service.getId())
                    .orElseThrow(() -> new RuntimeException("Environment not found"));

            //Ensure the variable name doesn't already exist
            for(Variable var : environment.getVariables()) {
                if(encryptionUtility.decrypt(var.getKey()).equals(variable.getKey()))
                    return ResponseEntity.status(400).body(new ErrorResponse("Variable with that key already exists in this service/environment"));
            }

            variable.setEnvironment(environment);

            //Encrypt the variables key/value pair
            variable.setKey(encryptionUtility.encrypt(variable.getKey()));
            variable.setValue(encryptionUtility.encrypt(variable.getValue()));

            variableRepository.save(variable);

            return ResponseEntity.ok(variable);
        } catch (RuntimeException runtimeException) {
            return ResponseEntity.status(400).body(new ErrorResponse(runtimeException.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(400).body(new ErrorResponse("Unable to add variable"));
        }
    }

    public ResponseEntity<?> deleteVariable(String serviceName, String environmentName, String variableName) {
        try {
            Service service = serviceRepository.findByName(serviceName)
                    .orElseThrow(() -> new RuntimeException("Service not found"));

            Environment environment = environmentRepository.findByNameAndServiceId(environmentName, service.getId())
                    .orElseThrow(() -> new RuntimeException("Environment not found"));

            Variable variable = variableRepository.findByEnvironmentIdAndKey(environment.getId(), encryptionUtility.encrypt(variableName))
                    .orElseThrow(() -> new RuntimeException("Variable not found"));

            variableRepository.deleteById(variable.getId());

            return ResponseEntity.ok("Variable successfully deleted");
        } catch (Exception ex) {
            return ResponseEntity.status(400).body(new ErrorResponse("Unable to delete variable"));
        }
    }

    public ResponseEntity<?> deleteEnvironment(String serviceName, String environmentName) {
        try {
            Service service = serviceRepository.findByName(serviceName)
                    .orElseThrow(() -> new RuntimeException("Service not found"));

            Environment environment = environmentRepository.findByNameAndServiceId(environmentName, service.getId())
                    .orElseThrow(() -> new RuntimeException("Environment not found"));

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
            Service service = serviceRepository.findByName(serviceName)
                    .orElseThrow(() -> new RuntimeException("Service not found"));

            serviceRepository.delete(service);

            return ResponseEntity.ok("Service successfully deleted");
        } catch (RuntimeException runtimeException) {
            return ResponseEntity.status(400).body(new ErrorResponse(runtimeException.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(400).body(new ErrorResponse("Unable to delete service"));
        }
    }
}
