package com.getreviews.engagements_service.dto;

import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EngagementsDTO {
    private Long id;

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be a positive number")
    private Long userId;

    @NotNull(message = "Task ID is required")
    @Positive(message = "Task ID must be a positive number")
    private Long taskId;

    private LocalDateTime completedAt;

    private int creditsEarned;

    private Long engagementsStatusId;
}
