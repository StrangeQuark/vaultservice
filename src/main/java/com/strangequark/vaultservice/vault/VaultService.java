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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class VaultService {
    private static final Logger LOGGER = LoggerFactory.getLogger(VaultService.class);

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
            LOGGER.info("Attempting to create service");

            if(serviceRepository.findByName(serviceName).isPresent()) {
                LOGGER.error("Service creation failed - That service name already exists");
                return ResponseEntity.status(400).body(new ErrorResponse("That service name already exists"));
            }

            Service service = new Service();
            service.setName(serviceName);

            Service savedService = serviceRepository.save(service);

            return ResponseEntity.ok(savedService);
        } catch (Exception ex) {
            LOGGER.error("Service creation failed");
            LOGGER.error(ex.getMessage());
            return ResponseEntity.status(400).body(new ErrorResponse("Service creation failed"));
        }
    }

    public ResponseEntity<?> createEnvironment(String serviceName, String environmentName) {
        try {
            LOGGER.info("Attempting to create environment");

            Service service = serviceRepository.findByName(serviceName)
                    .orElseThrow(() -> new RuntimeException("Service not found"));

            if(environmentRepository.findByNameAndServiceId(environmentName, service.getId()).isPresent()) {
                LOGGER.error("Environment creation failed - An environment with that name already exists in this service");
                return ResponseEntity.status(400).body(new ErrorResponse("Environment with that name already exists in this service"));
            }

            Environment environment = new Environment();
            environment.setName(environmentName);
            environment.setService(service);
            environmentRepository.save(environment);

            return ResponseEntity.ok(environment);
        } catch (RuntimeException runtimeException) {
            LOGGER.error("Runtime exception when attempting to create environment");
            LOGGER.error(runtimeException.getMessage());
            return ResponseEntity.status(400).body(new ErrorResponse("Environment creation failed - runtime exception"));
        } catch (Exception ex) {
            LOGGER.error("Environment creation failed");
            LOGGER.error(ex.getMessage());
            return ResponseEntity.status(400).body(new ErrorResponse("Environment creation failed"));
        }
    }

    public ResponseEntity<?> getService(String serviceName) {
        try {
            LOGGER.info("Attempting to get service");

            Service service = serviceRepository.findByName(serviceName)
                    .orElseThrow(() -> new RuntimeException("Service not found"));

            for(Environment env : service.getEnvironments()) {
                env.setVariables(encryptionUtility.decryptList(env.getVariables()));
            }

            return ResponseEntity.ok(service);
        } catch (RuntimeException runtimeException) {
            LOGGER.error("Runtime exception when getting service");
            LOGGER.error(runtimeException.getMessage());
            return ResponseEntity.status(400).body(new ErrorResponse("Service retrieval failed - runtime exception"));
        } catch (Exception ex) {
            LOGGER.error("Service retrieval failed");
            LOGGER.error(ex.getMessage());
            return ResponseEntity.status(400).body(new ErrorResponse("Service retrieval failed"));
        }
    }

    public ResponseEntity<?> getEnvironment(String serviceName, String environmentName) {
        try {
            LOGGER.info("Attempting to get environment");

            Service service = serviceRepository.findByName(serviceName)
                    .orElseThrow(() -> new RuntimeException("Service not found"));

            Environment environment = environmentRepository.findByNameAndServiceId(environmentName, service.getId())
                    .orElseThrow(() -> new RuntimeException("Environment not found"));

            environment.setVariables(encryptionUtility.decryptList(environment.getVariables()));

            return ResponseEntity.ok(environment);
        } catch (RuntimeException runtimeException) {
            LOGGER.error("Runtime exception when getting environment");
            LOGGER.error(runtimeException.getMessage());
            return ResponseEntity.status(400).body(new ErrorResponse("Environment retrieval failed - runtime exception"));
        } catch (Exception ex) {
            LOGGER.error("Environment retrieval failed");
            LOGGER.error(ex.getMessage());
            return ResponseEntity.status(400).body(new ErrorResponse("Environment retrieval failed"));
        }
    }

    public ResponseEntity<?> getVariablesByService(String serviceName) {
        try {
            LOGGER.info("Attempting to get variables by service");

            Service service = serviceRepository.findByName(serviceName)
                    .orElseThrow(() -> new RuntimeException("Service not found"));

            List<Variable> variables = variableRepository.findByEnvironmentServiceId(service.getId());

            return ResponseEntity.ok(encryptionUtility.decryptList(variables));
        } catch (RuntimeException runtimeException) {
            LOGGER.error("Runtime exception when getting variables by service");
            LOGGER.error(runtimeException.getMessage());
            return ResponseEntity.status(400).body(new ErrorResponse("Retrieval of variables by service failed - runtime exception"));
        } catch (Exception ex) {
            LOGGER.error("Retrieval of variables by service failed");
            LOGGER.error(ex.getMessage());
            return ResponseEntity.status(400).body(new ErrorResponse("Retrieval of variables by service failed"));
        }

    }

    public ResponseEntity<?> getVariablesByEnvironment(String serviceName, String environmentName) {
        try {
            LOGGER.info("Attempting to get variables by environment");

            Service service = serviceRepository.findByName(serviceName)
                    .orElseThrow(() -> new RuntimeException("Service not found"));

            Environment environment = environmentRepository.findByNameAndServiceId(environmentName, service.getId())
                    .orElseThrow(() -> new RuntimeException("Environment not found"));

            List<Variable> variables = variableRepository.findByEnvironmentId(environment.getId());

            return ResponseEntity.ok(encryptionUtility.decryptList(variables));
        } catch (RuntimeException runtimeException) {
            LOGGER.error("Runtime exception when getting variables by environment");
            LOGGER.error(runtimeException.getMessage());
            return ResponseEntity.status(400).body(new ErrorResponse("Retrieval of variables by environment failed - runtime exception"));
        } catch (Exception ex) {
            LOGGER.error("Retrieval of variables by environment failed");
            LOGGER.error(ex.getMessage());
            return ResponseEntity.status(400).body(new ErrorResponse("Retrieval of variables by environment failed"));
        }
    }

    public ResponseEntity<?> getVariableByName(String serviceName, String environmentName, String variableName) {
        try {
            LOGGER.info("Attempting to get variable by name");

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
            LOGGER.error("Runtime exception when getting variable by name");
            LOGGER.error(runtimeException.getMessage());
            return ResponseEntity.status(400).body(new ErrorResponse("Retrieval of variable by name failed - runtime exception"));
        } catch (Exception ex) {
            LOGGER.error("Unable to get variable by name");
            LOGGER.error(ex.getMessage());
            return ResponseEntity.status(400).body(new ErrorResponse("Retrieval of variable by name failed"));
        }
    }

    public ResponseEntity<?> addVariable(String serviceName, String environmentName, Variable variable) {
        try {
            LOGGER.info("Attempting to add variable");

            Service service = serviceRepository.findByName(serviceName)
                    .orElseThrow(() -> new RuntimeException("Service not found"));

            Environment environment = environmentRepository.findByNameAndServiceId(environmentName, service.getId())
                    .orElseThrow(() -> new RuntimeException("Environment not found"));

            //Ensure the variable name doesn't already exist
            for(Variable var : environment.getVariables()) {
                if(encryptionUtility.decrypt(var.getKey()).equals(variable.getKey())) {
                    LOGGER.error("Variable with that key already exists in this service/environment");
                    return ResponseEntity.status(400).body(new ErrorResponse("Variable with that key already exists in this service/environment"));
                }
            }

            variable.setEnvironment(environment);

            //Encrypt the variables key/value pair
            variable.setKey(encryptionUtility.encrypt(variable.getKey()));
            variable.setValue(encryptionUtility.encrypt(variable.getValue()));

            variableRepository.save(variable);

            return ResponseEntity.ok(variable);
        } catch (RuntimeException runtimeException) {
            LOGGER.error("Runtime exception when adding variable");
            LOGGER.error(runtimeException.getMessage());
            return ResponseEntity.status(400).body(new ErrorResponse("Unable to add variable - runtime exception"));
        } catch (Exception ex) {
            LOGGER.error("Exception when attempting to add variable");
            LOGGER.error(ex.getMessage());
            return ResponseEntity.status(400).body(new ErrorResponse("Unable to add variable"));
        }
    }

    public ResponseEntity<?> addEnvFile(String serviceName, String environmentName, MultipartFile file) {
        LOGGER.info("Attempting to upload env file");

        try {
            Service service = serviceRepository.findByName(serviceName)
                    .orElseThrow(() -> new RuntimeException("Service not found"));

            Environment environment = environmentRepository.findByNameAndServiceId(environmentName, service.getId())
                    .orElseThrow(() -> new RuntimeException("Environment not found"));

            String fileExtension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
            if (!fileExtension.equals("env")) {
                LOGGER.error("File extension is not .env");
                return ResponseEntity.status(400).body(new ErrorResponse("File extension is not .env"));
            }

            // Existing keys in decrypted form to skip duplicates
            Set<String> existingKeys = environment.getVariables().stream()
                    .map(var -> {
                        try {
                            return encryptionUtility.decrypt(var.getKey());
                        } catch (Exception ex) {
                            LOGGER.error("Decryption failed for a key");
                            LOGGER.error(ex.getMessage());
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            String line;
            int added = 0;
            int skipped = 0;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                // Skip empty lines and comments
                if (line.isEmpty() || line.startsWith("#")) continue;

                int equalsIndex = line.indexOf('=');
                if (equalsIndex == -1) continue; // Skip invalid lines

                String key = line.substring(0, equalsIndex).trim();
                String value = line.substring(equalsIndex + 1).trim();

                if (existingKeys.contains(key)) {
                    LOGGER.info("Skipping existing variable");
                    skipped++;
                    continue;
                }

                Variable variable = new Variable();
                variable.setEnvironment(environment);
                variable.setKey(encryptionUtility.encrypt(key));
                variable.setValue(encryptionUtility.encrypt(value));

                variableRepository.save(variable);
                added++;
            }

            LOGGER.info("File processed: " + added + " variables added, " + skipped + " skipped.");
            return ResponseEntity.ok("Variables added: " + added + ", Skipped: " + skipped);
        } catch (NullPointerException ex) {
            LOGGER.error("NPE - Invalid file extension");
            LOGGER.error(ex.getMessage());
            return ResponseEntity.status(400).body(new ErrorResponse("File upload failed"));
        } catch (Exception ex) {
            LOGGER.error("Unexpected error processing env file");
            LOGGER.error(ex.getMessage());
            return ResponseEntity.status(500).body(new ErrorResponse("Failed to process env file"));
        }
    }

    public ResponseEntity<?> downloadEnvFile(String serviceName, String environmentName) {
        try {
            LOGGER.info("Attempting to download .env file");

            Service service = serviceRepository.findByName(serviceName)
                    .orElseThrow(() -> new RuntimeException("Service not found"));

            Environment environment = environmentRepository.findByNameAndServiceId(environmentName, service.getId())
                    .orElseThrow(() -> new RuntimeException("Environment not found"));

            List<Variable> decryptedVariables = encryptionUtility.decryptList(variableRepository.findByEnvironmentId(environment.getId()));

            // Generate .env content
            StringBuilder envContent = new StringBuilder();
            for (Variable variable : decryptedVariables) {
                envContent.append(variable.getKey()).append("=").append(variable.getValue()).append("\n");
            }

            byte[] envBytes = envContent.toString().getBytes(StandardCharsets.UTF_8);
            ByteArrayResource resource = new ByteArrayResource(envBytes);

            String filename = serviceName + "-" + environmentName + ".env";

            LOGGER.info("Env file successfully downloaded");
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.TEXT_PLAIN)
                    .contentLength(envBytes.length)
                    .body(resource);
        } catch (RuntimeException runtimeException) {
            LOGGER.error("Runtime exception when downloading env file");
            LOGGER.error(runtimeException.getMessage());
            return ResponseEntity.status(400).body(new ErrorResponse("File download failed"));
        } catch (Exception ex) {
            LOGGER.error("Unexpected error downloading env file");
            LOGGER.error(ex.getMessage());
            return ResponseEntity.status(500).body(new ErrorResponse("Failed to download .env file"));
        }
    }

    public ResponseEntity<?> deleteVariable(String serviceName, String environmentName, String variableName) {
        try {
            LOGGER.info("Attempting to delete variable");

            Service service = serviceRepository.findByName(serviceName)
                    .orElseThrow(() -> new RuntimeException("Service not found"));

            Environment environment = environmentRepository.findByNameAndServiceId(environmentName, service.getId())
                    .orElseThrow(() -> new RuntimeException("Environment not found"));

            Variable variable = variableRepository.findByEnvironmentIdAndKey(environment.getId(), encryptionUtility.encrypt(variableName))
                    .orElseThrow(() -> new RuntimeException("Variable not found"));

            variableRepository.deleteById(variable.getId());

            LOGGER.info("Variable successfully deleted");
            return ResponseEntity.ok("Variable successfully deleted");
        } catch (RuntimeException runtimeException) {
            LOGGER.error("Runtime exception when deleting variable");
            LOGGER.error(runtimeException.getMessage());
            return ResponseEntity.status(400).body(new ErrorResponse("Variable deletion failed"));
        } catch (Exception ex) {
            LOGGER.error("Unable to delete variable");
            LOGGER.error(ex.getMessage());
            return ResponseEntity.status(400).body(new ErrorResponse("Unable to delete variable"));
        }
    }

    public ResponseEntity<?> deleteEnvironment(String serviceName, String environmentName) {
        try {
            LOGGER.info("Attempting to delete environment");

            Service service = serviceRepository.findByName(serviceName)
                    .orElseThrow(() -> new RuntimeException("Service not found"));

            Environment environment = environmentRepository.findByNameAndServiceId(environmentName, service.getId())
                    .orElseThrow(() -> new RuntimeException("Environment not found"));

            environmentRepository.delete(environment);

            LOGGER.info("Environment successfully deleted");
            return ResponseEntity.ok("Environment successfully deleted");
        } catch (RuntimeException runtimeException) {
            LOGGER.error("Runtime exception when deleting environment");
            LOGGER.error(runtimeException.getMessage());
            return ResponseEntity.status(400).body(new ErrorResponse("Environment deletion failed"));
        } catch (Exception ex) {
            LOGGER.error("Unable to delete environment");
            LOGGER.error(ex.getMessage());
            return ResponseEntity.status(400).body(new ErrorResponse("Unable to delete environment"));
        }
    }

    public ResponseEntity<?> deleteService(String serviceName) {
        try {
            LOGGER.info("Attempting to delete service");

            Service service = serviceRepository.findByName(serviceName)
                    .orElseThrow(() -> new RuntimeException("Service not found"));

            serviceRepository.delete(service);

            LOGGER.info("Service successfully deleted");
            return ResponseEntity.ok("Service successfully deleted");
        } catch (RuntimeException runtimeException) {
            LOGGER.error("Runtime exception when attempting to delete service");
            LOGGER.error(runtimeException.getMessage());
            return ResponseEntity.status(400).body(new ErrorResponse("Service deletion failed"));
        } catch (Exception ex) {
            LOGGER.error("Unable to delete service");
            LOGGER.error(ex.getMessage());
            return ResponseEntity.status(400).body(new ErrorResponse("Unable to delete service"));
        }
    }
}
