// Integration file: Auth

package com.strangequark.vaultservice.serviceuser;

import com.strangequark.vaultservice.service.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface ServiceUserRepository extends JpaRepository<ServiceUser, UUID> {
    ServiceUser findByUserIdAndServiceId(UUID userId, UUID serviceId);

    @Query("SELECT su.service FROM ServiceUser su WHERE su.userId = :userId")
    List<Service> findServicesByUserId(UUID userId);

    @Modifying
    @Transactional
    @Query("DELETE ServiceUser su WHERE su.userId = :userId AND su.service.id = :serviceId")
    void deleteServiceUser(UUID userId, UUID serviceId);
}
