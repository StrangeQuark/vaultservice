package com.strangequark.vaultservice.vault;

import com.strangequark.vaultservice.service.ServiceVariable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vault")
public class VaultController {

    @Autowired
    private VaultService vaultService;

    @GetMapping("/{serviceName}")
    public List<ServiceVariable> getVariablesByService(@PathVariable String serviceName) {
        return vaultService.getVariablesByService(serviceName);
    }

    @GetMapping("/{serviceName}/{environment}")
    public List<ServiceVariable> getVariablesByEnvironment(@PathVariable String serviceName, @PathVariable String environment) {
        return vaultService.getVariablesByEnvironment(serviceName, environment);
    }

    @GetMapping("/{serviceName}/{environment}/{key}")
    public ServiceVariable getVariable(@PathVariable String serviceName, @PathVariable String environment, @PathVariable String key) {
        return vaultService.getVariable(serviceName, environment, key);
    }

    @PostMapping("addVariable")
    public ServiceVariable addVariable(@RequestBody ServiceVariable variable) {
        return vaultService.addVariable(variable);
    }

    @DeleteMapping("/{id}")
    public void deleteVariable(@PathVariable Integer id) {
        vaultService.deleteVariable(id);
    }
}
