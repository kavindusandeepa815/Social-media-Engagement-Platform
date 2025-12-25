package com.getreviews.task_service.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class TaskRequest {

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be a positive number")
    private Long userId;

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @NotBlank(message = "Target URL is required")
    @Size(max = 500, message = "Target URL must not exceed 500 characters")
    @Pattern(
            regexp = "^(http|https)://.*$",
            message = "Target URL must start with http:// or https://"
    )
    private String targetUrl;

    @NotNull(message = "Required completions is required")
    @Min(value = 1, message = "Required completions must be at least 1")
    private Integer requiredCompletions;

    @NotNull(message = "Platform task type ID is required")
    @Positive(message = "Platform task type ID must be positive")
    private Long platformTaskTypeId;
}
