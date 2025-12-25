package com.getreviews.task_service.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {

    private Long id;

    private Long userId; //

    private String title; //

    private String targetUrl; //

    private Integer creditsOffered; //-

    private Integer requiredCompletions; //

    private Integer currentCompletions;

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    private Long platformTaskTypeId; //

    private Long taskStatusId; //-
}
