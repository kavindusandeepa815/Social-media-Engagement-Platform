package com.getreviews.engagements_service.service;

import com.getreviews.core_service.dto.EngagementCompleteDTO;
import com.getreviews.core_service.dto.UserResponse;
import com.getreviews.engagements_service.dto.EngagementsDTO;
import com.getreviews.engagements_service.dto.EngagementsRequestDTO;
import com.getreviews.engagements_service.dto.TaskDTO;
import com.getreviews.engagements_service.entity.Engagements;
import com.getreviews.engagements_service.entity.EngagementsStatus;
import com.getreviews.engagements_service.repository.EngagementsRepository;
import com.getreviews.engagements_service.repository.EngagementsStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated
@Slf4j
public class EngagementsService {

    private final EngagementsRepository engagementsRepository;
    private final EngagementsStatusRepository engagementsStatusRepository;
    private final WebClient.Builder webClientBuilder;
    private final KafkaProducerService kafkaProducerService;

    public EngagementsDTO addEngagements(EngagementsRequestDTO dto) {

        TaskDTO task;

        try {
            task = webClientBuilder.build()
                    .get()
                    .uri("http://task-service/api/task/{id}", dto.getTaskId())
                    .retrieve()
                    .bodyToMono(TaskDTO.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            throw new RuntimeException("Task not found with id: " + dto.getTaskId());
        } catch (WebClientResponseException e) {
            throw new RuntimeException(
                    "Task-service error: " + e.getStatusCode() + " " + e.getResponseBodyAsString()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to call task-service", e);
        }

        if (task == null) {
            throw new RuntimeException("Task not found");
        }

        // Check if already completed
        if (task.getCurrentCompletions() >= task.getRequiredCompletions()) {
            throw new RuntimeException("Task already completed");
        }


        UserResponse userResponse;

        try {
            userResponse = webClientBuilder.build()
                    .get()
                    .uri("http://user-service/api/user/{id}", dto.getUserId())
                    .retrieve()
                    .bodyToMono(UserResponse.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            throw new RuntimeException("User not found with id: " + dto.getUserId());
        } catch (WebClientResponseException e) {
            throw new RuntimeException(
                    "User-service error: " + e.getStatusCode() + " " + e.getResponseBodyAsString()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to call user-service", e);
        }

        if (userResponse == null) {
            throw new RuntimeException("User not found");
        }

        EngagementsStatus status = engagementsStatusRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Invalid engagement status"));

        Engagements engagement = Engagements.builder()
                .userId(userResponse.getId())
                .taskId(task.getId())
                .credits_earned(task.getCreditsOffered())
                .engagementsStatus(status)
                .build();

        Engagements savedEngagement = engagementsRepository.save(engagement);


        // Publish event to Kafka with error handling
        try {
            EngagementCompleteDTO event = EngagementCompleteDTO.builder()
                    .taskId(savedEngagement.getTaskId())
                    .build();

            kafkaProducerService.sendEngagementCompletedEvent(event);
            log.info("Engagement event sent to Kafka successfully");
        } catch (Exception e) {
            // Log the error but don't fail the engagement creation
            log.error("Failed to send engagement event to Kafka: {}", e.getMessage(), e);
        }

        return convertToDTO(savedEngagement);
    }

    public EngagementsDTO updateEngagementsStatus(Long engagementsId, Long statusId) {

        Engagements engagement = engagementsRepository.findById(engagementsId)
                .orElseThrow(() -> new RuntimeException("Engagement not found"));

        EngagementsStatus status = engagementsStatusRepository.findById(statusId)
                .orElseThrow(() -> new RuntimeException("Invalid status"));

        engagement.setEngagementsStatus(status);

        return convertToDTO(engagementsRepository.save(engagement));
    }

    public EngagementsDTO getEngagementsById(Long id) {
        Engagements engagement = engagementsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Engagement not found"));
        return convertToDTO(engagement);
    }

    public List<EngagementsDTO> getAllEngagements() {
        return engagementsRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private EngagementsDTO convertToDTO(Engagements engagement) {
        return EngagementsDTO.builder()
                .id(engagement.getId())
                .userId(engagement.getUserId())
                .taskId(engagement.getTaskId())
                .completedAt(engagement.getCompletedAt())
                .creditsEarned(engagement.getCredits_earned())
                .engagementsStatusId(engagement.getEngagementsStatus().getId())
                .build();
    }

}
