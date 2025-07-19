package com.strangequark.vaultservice.vault;

import com.strangequark.vaultservice.serviceuser.ServiceUserRequest;// Integration line: Auth
import com.strangequark.vaultservice.variable.Variable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/vault")
public class VaultController {

    @Autowired
    private VaultService vaultService;

    @PostMapping("/create-service/{serviceName}")
    public ResponseEntity<?> createService(@PathVariable String serviceName) {
        return vaultService.createService(serviceName);
    }

    @PostMapping("/create-environment/{serviceName}/{environmentName}")
    public ResponseEntity<?> createEnvironment(@PathVariable String serviceName, @PathVariable String environmentName) {
        return vaultService.createEnvironment(serviceName, environmentName);
    }

    @GetMapping("/get-service/{serviceName}")
    public ResponseEntity<?> getService(@PathVariable String serviceName) {
        return vaultService.getService(serviceName);
    }

    @GetMapping("/get-environment/{serviceName}/{environmentName}")
    public ResponseEntity<?> getEnvironment(
            @PathVariable String serviceName,
            @PathVariable String environmentName) {
        return vaultService.getEnvironment(serviceName, environmentName);
    }

    @GetMapping("/get-variables-by-service/{serviceName}")
    public ResponseEntity<?> getVariablesByService(@PathVariable String serviceName) {
        return vaultService.getVariablesByService(serviceName);
    }

    @GetMapping("/get-variables-by-environment/{serviceName}/{environmentName}")
    public ResponseEntity<?> getVariablesByEnvironment(
            @PathVariable String serviceName,
            @PathVariable String environmentName) {
        return vaultService.getVariablesByEnvironment(serviceName, environmentName);
    }

    @GetMapping("/get-variable-by-name/{serviceName}/{environmentName}/{variableName}")
    public ResponseEntity<?> getVariableByName(
            @PathVariable String serviceName,
            @PathVariable String environmentName,
            @PathVariable String variableName) {
        return vaultService.getVariableByName(serviceName, environmentName, variableName);
    }

    @PostMapping("/add-variable/{serviceName}/{environmentName}")
    public ResponseEntity<?> addVariable(
            @PathVariable String serviceName,
            @PathVariable String environmentName,
            @RequestBody Variable variable) {
        return vaultService.addVariable(serviceName, environmentName, variable);
    }

    @PostMapping("/update-variable/{serviceName}/{environmentName}")
    public ResponseEntity<?> updateVariable(
            @PathVariable String serviceName,
            @PathVariable String environmentName,
            @RequestBody Variable variable) {
        return vaultService.updateVariable(serviceName, environmentName, variable);
    }

    @PostMapping("/add-env-file/{serviceName}/{environmentName}")
    public ResponseEntity<?> addEnvFile(
            @PathVariable String serviceName,
            @PathVariable String environmentName,
            @RequestParam("file") MultipartFile file) {
        return vaultService.addEnvFile(serviceName, environmentName, file);
    }

    @GetMapping("/download-env-file/{serviceName}/{environmentName}")
    public ResponseEntity<?> downloadEnvFile(
            @PathVariable String serviceName,
            @PathVariable String environmentName) {
        return vaultService.downloadEnvFile(serviceName, environmentName);
    }

    @DeleteMapping("/delete-variable/{serviceName}/{environmentName}/{variableName}")
    public ResponseEntity<?> deleteVariable(
            @PathVariable String serviceName,
            @PathVariable String environmentName,
            @PathVariable String variableName) {
        return vaultService.deleteVariable(serviceName, environmentName, variableName);
    }

    @DeleteMapping("/delete-environment/{serviceName}/{environmentName}")
    public ResponseEntity<?> deleteEnvironment(
            @PathVariable String serviceName,
            @PathVariable String environmentName) {
        return vaultService.deleteEnvironment(serviceName, environmentName);
    }

    @DeleteMapping("/delete-service/{serviceName}")
    public ResponseEntity<?> deleteService(@PathVariable String serviceName) {
        return vaultService.deleteService(serviceName);
    }

    // Integration function start: Auth
    @PostMapping("/add-user-to-service")
    public ResponseEntity<?> addUserToService(@RequestBody ServiceUserRequest serviceUserRequest) {
        return vaultService.addUserToService(serviceUserRequest);
    }

    @PostMapping("/delete-user-from-service")
    public ResponseEntity<?> deleteUserFromService(@RequestBody ServiceUserRequest serviceUserRequest) {
        return vaultService.deleteUserFromService(serviceUserRequest);
    }// Integration function end: Auth
}
