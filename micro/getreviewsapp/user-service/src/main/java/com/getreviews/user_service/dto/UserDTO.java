package com.getreviews.user_service.dto;

import com.getreviews.user_service.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;

    private String name;

    private String email;

    private String password;

    private Role role;

    private String googleId;

    private String profilePicture;

    private boolean emailVerified;

    private LocalDateTime createdAt;

    private int creditBalance;

    private int trustScore;
}
