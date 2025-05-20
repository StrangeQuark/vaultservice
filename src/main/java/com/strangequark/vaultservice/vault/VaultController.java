package com.strangequark.vaultservice.vault;

import com.strangequark.vaultservice.variable.Variable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vault")
public class VaultController {

    @Autowired
    private VaultService vaultService;

    @PostMapping("/{serviceName}")
    public ResponseEntity<?> createService(@PathVariable String serviceName) {
        return vaultService.createService(serviceName);
    }

    @PostMapping("/{serviceName}/{environmentName}")
    public ResponseEntity<?> createEnvironment(@PathVariable String serviceName, @PathVariable String environmentName) {
        return vaultService.createEnvironment(serviceName, environmentName);
    }

    @GetMapping("/{serviceName}/variables")
    public ResponseEntity<?> getVariablesByService(@PathVariable String serviceName) {
        return vaultService.getVariablesByService(serviceName);
    }

    @GetMapping("/{serviceName}/{environmentName}/variables")
    public ResponseEntity<?> getVariablesByEnvironment(
            @PathVariable String serviceName,
            @PathVariable String environmentName) {
        return vaultService.getVariablesByEnvironment(serviceName, environmentName);
    }

    @GetMapping("/{serviceName}/{environmentName}/variables/{variableName}")
    public ResponseEntity<?> getVariableByName(
            @PathVariable String serviceName,
            @PathVariable String environmentName,
            @PathVariable String variableName) {
        return vaultService.getVariableByName(serviceName, environmentName, variableName);
    }

    @PostMapping("/{serviceName}/{environmentName}/variables")
    public ResponseEntity<?> addVariable(
            @PathVariable String serviceName,
            @PathVariable String environmentName,
            @RequestBody Variable variable) {
        return vaultService.addVariable(serviceName, environmentName, variable);
    }

    @DeleteMapping("/{serviceName}/{environmentName}/variables/{variableName}")
    public ResponseEntity<?> deleteVariable(
            @PathVariable String serviceName,
            @PathVariable String environmentName,
            @PathVariable String variableName) {
        return vaultService.deleteVariable(serviceName, environmentName, variableName);
    }

    @DeleteMapping("/{serviceName}/{environmentName}")
    public ResponseEntity<?> deleteEnvironment(
            @PathVariable String serviceName,
            @PathVariable String environmentName) {
        return vaultService.deleteEnvironment(serviceName, environmentName);
    }

    @DeleteMapping("/{serviceName}")
    public ResponseEntity<?> deleteService(@PathVariable String serviceName) {
        return vaultService.deleteService(serviceName);
    }
}
