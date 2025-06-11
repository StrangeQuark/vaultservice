package com.strangequark.vaultservice.vault;

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

    @PostMapping("/createService/{serviceName}")
    public ResponseEntity<?> createService(@PathVariable String serviceName) {
        return vaultService.createService(serviceName);
    }

    @PostMapping("/createEnvironment/{serviceName}/{environmentName}")
    public ResponseEntity<?> createEnvironment(@PathVariable String serviceName, @PathVariable String environmentName) {
        return vaultService.createEnvironment(serviceName, environmentName);
    }

    @GetMapping("/getService/{serviceName}")
    public ResponseEntity<?> getService(@PathVariable String serviceName) {
        return vaultService.getService(serviceName);
    }

    @GetMapping("/getEnvironment/{serviceName}/{environmentName}")
    public ResponseEntity<?> getEnvironment(
            @PathVariable String serviceName,
            @PathVariable String environmentName) {
        return vaultService.getEnvironment(serviceName, environmentName);
    }

    @GetMapping("/getVariablesByService/{serviceName}")
    public ResponseEntity<?> getVariablesByService(@PathVariable String serviceName) {
        return vaultService.getVariablesByService(serviceName);
    }

    @GetMapping("/getVariablesByEnvironment/{serviceName}/{environmentName}")
    public ResponseEntity<?> getVariablesByEnvironment(
            @PathVariable String serviceName,
            @PathVariable String environmentName) {
        return vaultService.getVariablesByEnvironment(serviceName, environmentName);
    }

    @GetMapping("/getVariableByName/{serviceName}/{environmentName}/{variableName}")
    public ResponseEntity<?> getVariableByName(
            @PathVariable String serviceName,
            @PathVariable String environmentName,
            @PathVariable String variableName) {
        return vaultService.getVariableByName(serviceName, environmentName, variableName);
    }

    @PostMapping("/addVariable/{serviceName}/{environmentName}")
    public ResponseEntity<?> addVariable(
            @PathVariable String serviceName,
            @PathVariable String environmentName,
            @RequestBody Variable variable) {
        return vaultService.addVariable(serviceName, environmentName, variable);
    }

    @PostMapping("/addEnvFile/{serviceName}/{environmentName}")
    public ResponseEntity<?> addEnvFile(
            @PathVariable String serviceName,
            @PathVariable String environmentName,
            @RequestParam("file") MultipartFile file) {
        return vaultService.addEnvFile(serviceName, environmentName, file);
    }

    @DeleteMapping("/deleteVariable/{serviceName}/{environmentName}/{variableName}")
    public ResponseEntity<?> deleteVariable(
            @PathVariable String serviceName,
            @PathVariable String environmentName,
            @PathVariable String variableName) {
        return vaultService.deleteVariable(serviceName, environmentName, variableName);
    }

    @DeleteMapping("/deleteEnvironment/{serviceName}/{environmentName}")
    public ResponseEntity<?> deleteEnvironment(
            @PathVariable String serviceName,
            @PathVariable String environmentName) {
        return vaultService.deleteEnvironment(serviceName, environmentName);
    }

    @DeleteMapping("/deleteService/{serviceName}")
    public ResponseEntity<?> deleteService(@PathVariable String serviceName) {
        return vaultService.deleteService(serviceName);
    }
}
