// Integration file: Auth

package com.strangequark.vaultservice.serviceuser;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.strangequark.vaultservice.service.Service;
import com.strangequark.vaultservice.utility.RoleEncryptDecryptConverter;
import jakarta.persistence.Entity;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "service_users")
public class ServiceUser {

    public ServiceUser() {

    }

    public ServiceUser(Service service, UUID userId, ServiceUserRole role) {
        this.service = service;
        this.userId = userId;
        this.role = role;
    }

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    @JsonBackReference
    private Service service;

    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Convert(converter = RoleEncryptDecryptConverter.class)
    private ServiceUserRole role;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public ServiceUserRole getRole() {
        return role;
    }

    public void setRole(ServiceUserRole role) {
        this.role = role;
    }
}
