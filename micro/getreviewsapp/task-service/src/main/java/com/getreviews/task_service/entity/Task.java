package com.getreviews.task_service.entity;

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
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @ManyToOne
//    @JoinColumn(name = "user_id", nullable = false)
    @Column(name = "user_id", nullable = false)
    private Long userId;

    private String title;

    private String target_url;

    private int credits_offered;

    private int required_completions;

    private int current_completions;

    private LocalDateTime created_at;

    private LocalDateTime expires_at;

    @ManyToOne
    @JoinColumn(name = "platform_task_type_id", nullable = false)
    private PlatformTaskType platformTaskType;

    @ManyToOne
    @JoinColumn(name = "task_status_id", nullable = false)
    private TaskStatus taskStatus;

    @PrePersist
    protected void onCreate() {
        created_at = LocalDateTime.now();
        current_completions = 0;
        expires_at = LocalDateTime.now().plusMonths(1);
    }

}
