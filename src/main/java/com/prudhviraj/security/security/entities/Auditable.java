package com.prudhviraj.security.security.entities;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Audited
public class Auditable {
    @CreationTimestamp
    private LocalDateTime createdAt;
    @CreatedBy
    private String createdBy;
    @UpdateTimestamp
    private  LocalDateTime updatedAt;
    @LastModifiedBy
    private String updatedBy;

}
