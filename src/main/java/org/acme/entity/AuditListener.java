package org.acme.entity;

import io.quarkus.arc.Arc;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

public class AuditListener {

    @PrePersist
    public void setCreatedBy(Object entity) {
        if (entity instanceof CoreEntity coreEntity) {
            String actor = getCurrentActor();
            coreEntity.createdBy = actor;
            coreEntity.updatedBy = actor;
        }
    }

    @PreUpdate
    public void setUpdatedBy(Object entity) {
        if (entity instanceof CoreEntity coreEntity) {
            coreEntity.updatedBy = getCurrentActor();
        }
    }

    private String getCurrentActor() {
        try {
            SecurityIdentity securityIdentity = Arc.container().instance(SecurityIdentity.class).get();
            if (securityIdentity == null || securityIdentity.isAnonymous()) {
                return "system";
            }
            String principalName = securityIdentity.getPrincipal().getName();
            return (principalName != null && !principalName.isBlank()) ? principalName : "system";
        } catch (Exception e) {
            // Fallback if CDI or SecurityIdentity is not available
            return "system";
        }
    }
}
