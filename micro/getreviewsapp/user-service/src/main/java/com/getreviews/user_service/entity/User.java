package com.getreviews.user_service.entity;

import com.getreviews.user_service.model.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 2, max = 50, message = "Password must be between 2 and 50 characters")
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String googleId;

    private String profilePicture;

    @Column(name = "email_verified")
    private boolean emailVerified = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "credit_balance")
    private int creditBalance = 100;

    @Column(name = "trust_score")
    private int trustScore = 50;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

}
