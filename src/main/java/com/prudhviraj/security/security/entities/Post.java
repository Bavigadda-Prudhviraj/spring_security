package com.prudhviraj.security.security.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Audited
public class Post extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String title;
    @Column(length = 5000)
    private String description;
    @ManyToOne
    @NotAudited // or @Audited(targetAuditMode = NOT_AUDITED)
    private User author;
}
