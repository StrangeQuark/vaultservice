package com.strangequark.vaultservice.vault;

import com.strangequark.vaultservice.variable.Variable;
import com.strangequark.vaultservice.environment.Environment;
import com.strangequark.vaultservice.service.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vault")
public class VaultController {

    @Autowired
    private VaultService vaultService;

    @PostMapping("/{serviceName}")
    public Service createService(@PathVariable String serviceName) {
        return vaultService.createService(serviceName);
    }

    @PostMapping("/{serviceName}/{environmentName}")
    public Environment createEnvironment(@PathVariable String serviceName, @PathVariable String environmentName) {
        return vaultService.createEnvironment(serviceName, environmentName);
    }

    @GetMapping("/{serviceName}/variables")
    public List<Variable> getVariablesByService(@PathVariable String serviceName) {
        return vaultService.getVariablesByService(serviceName);
    }

    @GetMapping("/{serviceName}/{environmentName}/variables")
    public List<Variable> getVariablesByEnvironment(
            @PathVariable String serviceName,
            @PathVariable String environmentName) {
        return vaultService.getVariablesByEnvironment(serviceName, environmentName);
    }

    @GetMapping("/{serviceName}/{environmentName}/variables/{variableName}")
    public Variable getVariableByName(
            @PathVariable String serviceName,
            @PathVariable String environmentName,
            @PathVariable String variableName) {
        return vaultService.getVariableByName(serviceName, environmentName, variableName);
    }

    @GetMapping("/variables/{id}")
    public Variable getVariable(@PathVariable Long id) {
        return vaultService.getVariable(id);
    }

    @PostMapping("/{serviceName}/{environmentName}/variables")
    public Variable addVariable(
            @PathVariable String serviceName,
            @PathVariable String environmentName,
            @RequestBody Variable variable) {
        return vaultService.addVariable(serviceName, environmentName, variable);
    }

    @DeleteMapping("/variables/{id}")
    public void deleteVariable(@PathVariable Long id) {
        vaultService.deleteVariable(id);
    }

    @DeleteMapping("/{serviceName}/{environmentName}")
    public void deleteEnvironment(
            @PathVariable String serviceName,
            @PathVariable String environmentName) {
        vaultService.deleteEnvironment(serviceName, environmentName);
    }

    @DeleteMapping("/{serviceName}")
    public void deleteService(@PathVariable String serviceName) {
        vaultService.deleteService(serviceName);
    }
}
