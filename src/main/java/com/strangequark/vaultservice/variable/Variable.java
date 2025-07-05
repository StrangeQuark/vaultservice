package com.strangequark.vaultservice.variable;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.strangequark.vaultservice.environment.Environment;

import com.strangequark.vaultservice.utility.EncryptDecryptConverter;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "variables")
public class Variable {

    public Variable() {

    }

    public Variable(Environment environment, String key, String value) {
        this.environment = environment;
        this.key = key;
        this.value = value;
    }

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "environment_id", nullable = false)
    @JsonBackReference
    private Environment environment;

    @Column(name = "var_key", nullable = false)
    @Convert(converter = EncryptDecryptConverter.class)
    private String key;

    @Column(name = "var_value")
    @Convert(converter = EncryptDecryptConverter.class)
    private String value;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

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

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
}

