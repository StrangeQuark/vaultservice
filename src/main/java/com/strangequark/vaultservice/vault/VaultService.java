package com.strangequark.vaultservice.vault;

import com.strangequark.vaultservice.environment.Environment;
import com.strangequark.vaultservice.error.ErrorResponse;
import com.strangequark.vaultservice.service.Service;
import com.strangequark.vaultservice.serviceuser.ServiceUser;// Integration line: Auth
import com.strangequark.vaultservice.serviceuser.ServiceUserRepository;// Integration line: Auth
import com.strangequark.vaultservice.serviceuser.ServiceUserRequest;// Integration line: Auth
import com.strangequark.vaultservice.serviceuser.ServiceUserRole;// Integration line: Auth
import com.strangequark.vaultservice.utility.AuthUtility;
import com.strangequark.vaultservice.utility.JwtUtility;
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
import java.util.UUID;
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
    @Autowired// Integration function start: Auth
    private ServiceUserRepository serviceUserRepository;
    @Autowired
    JwtUtility jwtUtility;
    @Autowired
    AuthUtility authUtility;// Integration function end: Auth

    @Transactional
    public ResponseEntity<?> createService(String serviceName) {
        try {
            LOGGER.info("Attempting to create service");

            if(serviceRepository.findByName(serviceName).isPresent()) {
                LOGGER.error("Service creation failed - That service name already exists");
                return ResponseEntity.status(400).body(new ErrorResponse("Service with that name already exists"));
            }

            Service service = new Service();
            service.setName(serviceName);
            service.addUser(new ServiceUser(service, UUID.fromString(jwtUtility.extractId()), ServiceUserRole.OWNER));// Integration line: Auth

            serviceRepository.save(service);

            LOGGER.info("New service successfully created");
            return ResponseEntity.ok("New service successfully created");
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            return ResponseEntity.status(400).body(new ErrorResponse(ex.getMessage()));
        }
    }

    @Transactional
    public ResponseEntity<?> createEnvironment(String serviceName, String environmentName) {
        try {
            LOGGER.info("Attempting to create environment");

            Service service = serviceRepository.findByName(serviceName)
                    .orElseThrow(() -> new RuntimeException("Service not found"));

            //Integration function start: Auth
            ServiceUser requestingUser = serviceUserRepository.findByUserIdAndServiceId(UUID.fromString(jwtUtility.extractId()), service.getId());

            // Ensure that the request user has access to this service and has the OWNER or MAINTAINER role
            if (requestingUser == null || (requestingUser.getRole() != ServiceUserRole.OWNER && requestingUser.getRole() != ServiceUserRole.MANAGER)) {
                throw new RuntimeException("Only service users with OWNER or MANAGER roles can create environments");
            }//Integration function end: Auth

            if(environmentRepository.findByNameAndServiceId(environmentName, service.getId()).isPresent()) {
                LOGGER.error("Environment creation failed - An environment with that name already exists in this service");
                return ResponseEntity.status(400).body(new ErrorResponse("Environment with that name already exists in this service"));
            }

            Environment environment = new Environment();
            environment.setName(environmentName);
            environment.setService(service);
            environmentRepository.save(environment);

            return ResponseEntity.ok(environment);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            return ResponseEntity.status(400).body(new ErrorResponse(ex.getMessage()));
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getService(String serviceName) {
        try {
            LOGGER.info("Attempting to get service");

            Service service = serviceRepository.findByName(serviceName)
                    .orElseThrow(() -> new RuntimeException("Service not found"));

            //Integration function start: Auth
            ServiceUser requestingUser = serviceUserRepository.findByUserIdAndServiceId(UUID.fromString(jwtUtility.extractId()), service.getId());

            // Ensure that the request user has access to this service
            if (requestingUser == null) {
                throw new RuntimeException("You do not have access to view this service");
            }//Integration function end: Auth

            return ResponseEntity.ok(service);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            return ResponseEntity.status(400).body(new ErrorResponse(ex.getMessage()));
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getEnvironment(String serviceName, String environmentName) {
        try {
            LOGGER.info("Attempting to get environment");

            Service service = serviceRepository.findByName(serviceName)
                    .orElseThrow(() -> new RuntimeException("Service not found"));

            //Integration function start: Auth
            ServiceUser requestingUser = serviceUserRepository.findByUserIdAndServiceId(UUID.fromString(jwtUtility.extractId()), service.getId());

            // Ensure that the request user has access to this service
            if (requestingUser == null) {
                throw new RuntimeException("You do not have access to view this service");
            }//Integration function end: Auth

            Environment environment = environmentRepository.findByNameAndServiceId(environmentName, service.getId())
                    .orElseThrow(() -> new RuntimeException("Environment not found"));

            return ResponseEntity.ok(environment);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            return ResponseEntity.status(400).body(new ErrorResponse(ex.getMessage()));
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getVariablesByService(String serviceName) {
        try {
            LOGGER.info("Attempting to get variables by service");

            Service service = serviceRepository.findByName(serviceName)
                    .orElseThrow(() -> new RuntimeException("Service not found"));

            //Integration function start: Auth
            ServiceUser requestingUser = serviceUserRepository.findByUserIdAndServiceId(UUID.fromString(jwtUtility.extractId()), service.getId());

            // Ensure that the request user has access to this service
            if (requestingUser == null) {
                throw new RuntimeException("You do not have access to view this service");
            }//Integration function end: Auth

            List<Variable> variables = variableRepository.findByEnvironmentServiceId(service.getId());

            return ResponseEntity.ok(variables);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            return ResponseEntity.status(400).body(new ErrorResponse(ex.getMessage()));
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getVariablesByEnvironment(String serviceName, String environmentName) {
        try {
            LOGGER.info("Attempting to get variables by environment");

            Service service = serviceRepository.findByName(serviceName)
                    .orElseThrow(() -> new RuntimeException("Service not found"));

            //Integration function start: Auth
            ServiceUser requestingUser = serviceUserRepository.findByUserIdAndServiceId(UUID.fromString(jwtUtility.extractId()), service.getId());

            // Ensure that the request user has access to this service
            if (requestingUser == null) {
                throw new RuntimeException("You do not have access to view this service");
            }//Integration function end: Auth

            Environment environment = environmentRepository.findByNameAndServiceId(environmentName, service.getId())
                    .orElseThrow(() -> new RuntimeException("Environment not found"));

            List<Variable> variables = variableRepository.findByEnvironmentId(environment.getId());

            return ResponseEntity.ok(variables);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            return ResponseEntity.status(400).body(new ErrorResponse(ex.getMessage()));
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getVariableByName(String serviceName, String environmentName, String variableName) {
        try {
            LOGGER.info("Attempting to get variable by name");

            Service service = serviceRepository.findByName(serviceName)
                    .orElseThrow(() -> new RuntimeException("Service not found"));

            //Integration function start: Auth
            ServiceUser requestingUser = serviceUserRepository.findByUserIdAndServiceId(UUID.fromString(jwtUtility.extractId()), service.getId());

            // Ensure that the request user has access to this service
            if (requestingUser == null) {
                throw new RuntimeException("You do not have access to view this service");
            }//Integration function end: Auth

            Environment environment = environmentRepository.findByNameAndServiceId(environmentName, service.getId())
                    .orElseThrow(() -> new RuntimeException("Environment not found"));

            Variable variable = variableRepository.findByEnvironmentIdAndKey(environment.getId(), variableName)
                    .orElseThrow(() -> new RuntimeException("Variable not found"));

            return ResponseEntity.ok(new VariableResponse(variable));
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            return ResponseEntity.status(400).body(new ErrorResponse(ex.getMessage()));
        }
    }

    @Transactional
    public ResponseEntity<?> addVariable(String serviceName, String environmentName, Variable variable) {
        try {
            LOGGER.info("Attempting to add variable");

            Service service = serviceRepository.findByName(serviceName)
                    .orElseThrow(() -> new RuntimeException("Service not found"));

            //Integration function start: Auth
            ServiceUser requestingUser = serviceUserRepository.findByUserIdAndServiceId(UUID.fromString(jwtUtility.extractId()), service.getId());

            // Ensure that the request user has access to this service
            if (requestingUser == null) {
                throw new RuntimeException("You do not have access to this service");
            }//Integration function end: Auth

            Environment environment = environmentRepository.findByNameAndServiceId(environmentName, service.getId())
                    .orElseThrow(() -> new RuntimeException("Environment not found"));

            //Ensure the variable name doesn't already exist
            if(variableRepository.findByEnvironmentIdAndKey(environment.getId(), variable.getKey()).isPresent()) {
                LOGGER.error("Variable with that key already exists in this service/environment");
                return ResponseEntity.status(400).body(new ErrorResponse("Variable with that key already exists in this service/environment"));
            }

            variable.setEnvironment(environment);
            variable.setLastUpdatedBy(requestingUser.getUserId());// Integration line: Auth
            variableRepository.save(variable);

            return ResponseEntity.ok(variable);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            return ResponseEntity.status(400).body(new ErrorResponse(ex.getMessage()));
        }
    }

    @Transactional
    public ResponseEntity<?> updateVariable(String serviceName, String environmentName, Variable variable) {
        try {
            LOGGER.info("Attempting to update variable");

            Service service = serviceRepository.findByName(serviceName)
                    .orElseThrow(() -> new RuntimeException("Service not found"));

            //Integration function start: Auth
            ServiceUser requestingUser = serviceUserRepository.findByUserIdAndServiceId(UUID.fromString(jwtUtility.extractId()), service.getId());

            // Ensure that the request user has access to this service
            if (requestingUser == null) {
                throw new RuntimeException("You do not have access to this service");
            }//Integration function end: Auth

            Environment environment = environmentRepository.findByNameAndServiceId(environmentName, service.getId())
                    .orElseThrow(() -> new RuntimeException("Environment not found"));

            Variable var = variableRepository.findByEnvironmentIdAndKey(environment.getId(), variable.getKey())
                    .orElseThrow(() -> new RuntimeException("Variable not found"));

            var.setValue(variable.getValue());
            var.setLastUpdatedBy(requestingUser.getUserId());// Integration line: Auth
            variableRepository.save(var);

            return ResponseEntity.ok(var);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            return ResponseEntity.status(400).body(new ErrorResponse(ex.getMessage()));
        }
    }

    @Transactional
    public ResponseEntity<?> addEnvFile(String serviceName, String environmentName, MultipartFile file) {
        LOGGER.info("Attempting to upload env file");

        try {
            Service service = serviceRepository.findByName(serviceName)
                    .orElseThrow(() -> new RuntimeException("Service not found"));

            //Integration function start: Auth
            ServiceUser requestingUser = serviceUserRepository.findByUserIdAndServiceId(UUID.fromString(jwtUtility.extractId()), service.getId());

            // Ensure that the request user has access to this service
            if (requestingUser == null) {
                throw new RuntimeException("You do not have access to this service");
            }//Integration function end: Auth

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
                            return var.getKey();
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
                variable.setKey(key);
                variable.setValue(value);
                variable.setLastUpdatedBy(requestingUser.getUserId());// Integration line: Auth
                variableRepository.save(variable);
                added++;
            }

            LOGGER.info("File processed: " + added + " variables added, " + skipped + " skipped.");
            return ResponseEntity.ok("Variables added: " + added + ", Skipped: " + skipped);
        } catch (NullPointerException ex) {
            LOGGER.error("NPE - Invalid file extension");
            LOGGER.error(ex.getMessage());
            return ResponseEntity.status(400).body(new ErrorResponse("Invalid file extension"));
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            return ResponseEntity.status(500).body(new ErrorResponse(ex.getMessage()));
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> downloadEnvFile(String serviceName, String environmentName) {
        try {
            LOGGER.info("Attempting to download .env file");

            Service service = serviceRepository.findByName(serviceName)
                    .orElseThrow(() -> new RuntimeException("Service not found"));

            //Integration function start: Auth
            ServiceUser requestingUser = serviceUserRepository.findByUserIdAndServiceId(UUID.fromString(jwtUtility.extractId()), service.getId());

            // Ensure that the request user has access to this service
            if (requestingUser == null) {
                throw new RuntimeException("You do not have access to this service");
            }//Integration function end: Auth

            Environment environment = environmentRepository.findByNameAndServiceId(environmentName, service.getId())
                    .orElseThrow(() -> new RuntimeException("Environment not found"));

            List<Variable> decryptedVariables = variableRepository.findByEnvironmentId(environment.getId());

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
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            return ResponseEntity.status(500).body(new ErrorResponse(ex.getMessage()));
        }
    }

    @Transactional
    public ResponseEntity<?> deleteVariable(String serviceName, String environmentName, String variableName) {
        try {
            LOGGER.info("Attempting to delete variable");

            Service service = serviceRepository.findByName(serviceName)
                    .orElseThrow(() -> new RuntimeException("Service not found"));

            //Integration function start: Auth
            ServiceUser requestingUser = serviceUserRepository.findByUserIdAndServiceId(UUID.fromString(jwtUtility.extractId()), service.getId());

            // Ensure that the request user has access to this service
            if (requestingUser == null) {
                throw new RuntimeException("You do not have access to this service");
            }//Integration function end: Auth

            Environment environment = environmentRepository.findByNameAndServiceId(environmentName, service.getId())
                    .orElseThrow(() -> new RuntimeException("Environment not found"));

            Variable variable = variableRepository.findByEnvironmentIdAndKey(environment.getId(), variableName)
                    .orElseThrow(() -> new RuntimeException("Variable not found"));

            variableRepository.deleteById(variable.getId());

            LOGGER.info("Variable successfully deleted");
            return ResponseEntity.ok("Variable successfully deleted");
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            return ResponseEntity.status(400).body(new ErrorResponse(ex.getMessage()));
        }
    }

    @Transactional
    public ResponseEntity<?> deleteEnvironment(String serviceName, String environmentName) {
        try {
            LOGGER.info("Attempting to delete environment");

            Service service = serviceRepository.findByName(serviceName)
                    .orElseThrow(() -> new RuntimeException("Service not found"));

            //Integration function start: Auth
            ServiceUser requestingUser = serviceUserRepository.findByUserIdAndServiceId(UUID.fromString(jwtUtility.extractId()), service.getId());

            // Ensure that the request user has access to this service and has the OWNER or MAINTAINER role
            if (requestingUser == null || (requestingUser.getRole() != ServiceUserRole.OWNER && requestingUser.getRole() != ServiceUserRole.MANAGER)) {
                throw new RuntimeException("Only service users with OWNER or MANAGER roles can delete environments");
            }//Integration function end: Auth

            Environment environment = environmentRepository.findByNameAndServiceId(environmentName, service.getId())
                    .orElseThrow(() -> new RuntimeException("Environment not found"));

            environmentRepository.delete(environment);

            LOGGER.info("Environment successfully deleted");
            return ResponseEntity.ok("Environment successfully deleted");
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            return ResponseEntity.status(400).body(new ErrorResponse(ex.getMessage()));
        }
    }

    @Transactional
    public ResponseEntity<?> deleteService(String serviceName) {
        try {
            LOGGER.info("Attempting to delete service");

            Service service = serviceRepository.findByName(serviceName)
                    .orElseThrow(() -> new RuntimeException("Service not found"));

            //Integration function start: Auth
            ServiceUser requestingUser = serviceUserRepository.findByUserIdAndServiceId(UUID.fromString(jwtUtility.extractId()), service.getId());

            // Ensure that the request user has access to this service and has the OWNER
            if (requestingUser == null || requestingUser.getRole() != ServiceUserRole.OWNER) {
                throw new RuntimeException("Only service users with OWNER role can delete services");
            }//Integration function end: Auth

            serviceRepository.delete(service);

            LOGGER.info("Service successfully deleted");
            return ResponseEntity.ok("Service successfully deleted");
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            return ResponseEntity.status(400).body(new ErrorResponse(ex.getMessage()));
        }
    }

    // Integration function start: Auth
    @Transactional
    public ResponseEntity<?> addUserToService(ServiceUserRequest serviceUserRequest) {
        LOGGER.info("Attempting to add user to service");

        try {
            Service service = serviceRepository.findByName(serviceUserRequest.getServiceName())
                    .orElseThrow(() -> new RuntimeException("Service with this name does not exist"));

            ServiceUser requestingUser = serviceUserRepository.findByUserIdAndServiceId(UUID.fromString(jwtUtility.extractId()), service.getId());

            // Ensure that the request user has access to this service and has the OWNER
            if (requestingUser == null || requestingUser.getRole() != ServiceUserRole.OWNER) {
                throw new RuntimeException("Only service users with OWNER role can add users to services");
            }

            // Ensure the target user exists
            String userIdStr = authUtility.getUserId(serviceUserRequest.getUsername());
            if (userIdStr == null) {
                throw new RuntimeException("Unable to retrieve user id");
            }
            UUID userId = UUID.fromString(userIdStr);

            // Avoid duplicate users
            if(serviceUserRepository.findByUserIdAndServiceId(userId, service.getId()) != null) {
                throw new RuntimeException("User is already part of this service");
            }

            service.addUser(new ServiceUser(service, userId, serviceUserRequest.getRole()));

            serviceRepository.save(service);

            LOGGER.info("User successfully added to service");
            return ResponseEntity.ok("User successfully added to service");
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            return ResponseEntity.status(400).body(new ErrorResponse(ex.getMessage()));
        }
    }

    @Transactional(readOnly = false)
    public ResponseEntity<?> deleteUserFromService(ServiceUserRequest serviceUserRequest) {
        LOGGER.info("Attempting to delete user from service");

        try {
            Service service = serviceRepository.findByName(serviceUserRequest.getServiceName())
                    .orElseThrow(() -> new RuntimeException("Service with this name does not exist"));

            ServiceUser requestingUser = serviceUserRepository.findByUserIdAndServiceId(UUID.fromString(jwtUtility.extractId()), service.getId());

            // Check if the user making the request belongs to the service
            if (requestingUser == null) {
                throw new RuntimeException("Requesting user does not have access to this service");
            }

            // Ensure the target user exists
            String userIdStr = authUtility.getUserId(serviceUserRequest.getUsername());
            if (userIdStr == null) {
                throw new RuntimeException("Unable to retrieve user id");
            }
            UUID userId = UUID.fromString(userIdStr);

            ServiceUser targetUser = serviceUserRepository.findByUserIdAndServiceId(userId, service.getId());

            // Check if the target user of the request belongs to the service
            if (targetUser == null) {
                throw new RuntimeException("Target user is not part of this service.");
            }

            // Check if the requesting user is either attempting to remove self or is an OWNER
            if(!requestingUser.getUserId().equals(targetUser.getUserId()) || requestingUser.getRole() != ServiceUserRole.OWNER) {
                throw new RuntimeException("Only OWNER users can remove others");
            }

            // If the target user has an OWNER role, we must ensure that we're not removing the last OWNER from the service
            if(targetUser.getRole() == ServiceUserRole.OWNER) {
                long ownerCount = service.getServiceUsers().stream()
                        .filter(su -> su.getRole() == ServiceUserRole.OWNER)
                        .count();

                if (ownerCount <= 1) {
                    throw new RuntimeException("Cannot remove the last OWNER from the service.");
                }
            }

            serviceUserRepository.deleteServiceUser(userId, service.getId());

            LOGGER.info("User successfully deleted from service");
            return ResponseEntity.ok("User successfully deleted from service");
        } catch(RuntimeException ex) {
            LOGGER.error(ex.getMessage());
            return ResponseEntity.status(400).body(new ErrorResponse(ex.getMessage()));
        }
    }// Integration function end: Auth
}
