package com.strangequark.vaultservice.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServiceVariableRepository extends JpaRepository<ServiceVariable, Integer> {
    List<ServiceVariable> findByServiceName(String serviceName);
    List<ServiceVariable> findByServiceNameAndEnvironment(String serviceName, String environment);
    ServiceVariable findByServiceNameAndEnvironmentAndKey(String serviceName, String environment, String key);
}
