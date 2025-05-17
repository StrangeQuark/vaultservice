package com.strangequark.vaultservice.service;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<Service, Long> {
    Service findByName(String name);
}
