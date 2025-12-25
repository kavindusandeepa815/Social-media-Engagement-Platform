package com.getreviews.engagements_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
public class Engagements {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @ManyToOne
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;

    @Column(name = "user_id", nullable = false)
    private Long userId;

//    @ManyToOne
//    @JoinColumn(name = "task_id", nullable = false)
//    private Task task;

    @Column(name = "task_id", nullable = false)
    private Long taskId;

    private LocalDateTime completedAt;

    @NotNull(message = "Credits offered is required")
    @Min(value = 1, message = "Credits offered must be at least 1")
    private int credits_earned;

    @ManyToOne
    @JoinColumn(name = "engagements_status_id", nullable = false)
    private EngagementsStatus engagementsStatus;

    @PrePersist
    protected void onCreate() {
        completedAt = LocalDateTime.now();
    }
}
