// Integration file: Auth

package com.strangequark.vaultservice.serviceuser;

import java.util.UUID;

public class ServiceUserResponse {
    private UUID userId;
    private ServiceUserRole role;

    public ServiceUserResponse(UUID userId, ServiceUserRole role) {
        this.userId = userId;
        this.role = role;
    }

    public UUID getUserId() {
        return userId;
    }

    public ServiceUserRole getRole() {
        return role;
    }
}
