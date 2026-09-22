package com.easyexpenses.api.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(
        name = "user",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_identity_issuer_subject",
                        columnNames = {"identityIssuer", "identitySubject"}
                )
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "identityIssuer", nullable = false)
    private String identityIssuer;

    @Column(name = "identitySubject", nullable = false)
    private String identitySubject;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @OneToOne(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private UserProfile userProfile;

    public void setUserProfile(UserProfile userProfile){
        this.userProfile = userProfile;
        userProfile.setUser(this);
    }
}
