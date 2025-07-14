// Integration file: Auth

package com.strangequark.vaultservice.serviceuser;

import java.util.UUID;

public class ServiceUserRequest {
    private String serviceName;
    private UUID userId;
    private ServiceUserRole role;

    public ServiceUserRequest() {

    }

    public ServiceUserRequest(String serviceName, UUID userId, ServiceUserRole role) {
        this.serviceName = serviceName;
        this.userId = userId;
        this.role = role;
    }


    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
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
