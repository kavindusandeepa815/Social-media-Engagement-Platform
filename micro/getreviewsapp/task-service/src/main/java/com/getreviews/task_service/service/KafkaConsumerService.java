package com.getreviews.task_service.service;

import com.getreviews.core_service.dto.EngagementCompleteDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

    private final TaskService taskService;

    @KafkaListener(topics = "engagement-completed", groupId = "task-service-group")
    public void consumeEngagementCompletedEvent(EngagementCompleteDTO event) {
        log.info("Received engagement completed event: {}", event);

        try {
            taskService.engagementTaskCompletion(event.getTaskId());
            log.info("Successfully updated task completion for taskId: {}", event.getTaskId());
        } catch (Exception e) {
            log.error("Error processing engagement completed event: {}", e.getMessage(), e);
            // You might want to implement retry logic or dead letter queue here
        }
    }
}
