package com.strangequark.vaultservice.service;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.strangequark.vaultservice.environment.Environment;

import com.strangequark.vaultservice.serviceuser.ServiceUser;
import com.strangequark.vaultservice.utility.EncryptDecryptConverter;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "services")
public class Service {

    public Service() {
        this.serviceUsers = new ArrayList<>();// Integration line: Auth
    }

    public Service(String name) {
        this.name = name;
    }

    public Service(String name, List<Environment> environments) {
        this(name);
        this.environments = environments;
    }

    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true, nullable = false)
    @Convert(converter = EncryptDecryptConverter.class)
    private String name;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Environment> environments;

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true)// Integration function start: Auth
    @JsonManagedReference
    private List<ServiceUser> serviceUsers;// Integration function end: Auth

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Environment> getEnvironments() {
        return environments;
    }

    public void setEnvironments(List<Environment> environments) {
        this.environments = environments;
    }

    // Integration function start: Auth
    public List<ServiceUser> getServiceUsers() {
        return serviceUsers;
    }

    public void setServiceUsers(List<ServiceUser> serviceUsers) {
        this.serviceUsers = serviceUsers;
    }

    public void addUser(ServiceUser serviceUser) {
        this.serviceUsers.add(serviceUser);
    }// Integration function end: Auth
}
