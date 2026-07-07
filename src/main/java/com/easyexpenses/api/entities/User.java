package com.easyexpenses.api.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String provider;

    @Column(nullable = false)
    private String provider_id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;
}
