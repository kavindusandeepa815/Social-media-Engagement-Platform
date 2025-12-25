package com.getreviews.task_service.service;

import com.getreviews.core_service.dto.UserResponse;
import com.getreviews.task_service.dto.TaskDTO;
import com.getreviews.task_service.dto.TaskRequest;
import com.getreviews.task_service.entity.*;
import com.getreviews.task_service.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated
public class TaskService {

    private final TaskRepository taskRepository;
    private final PlatformTaskTypeRepository platformTaskTypeRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final PlatformRepository platformRepository;
    private final TaskTypeRepository taskTypeRepository;
    private final WebClient.Builder webClientBuilder;

    @Transactional
    public void addTask(TaskRequest taskRequest) {

        TaskDTO taskDTO = convertTaskRequestToDTO(taskRequest);

        Task task = covertToEntity(taskDTO);

        Task savedTask = taskRepository.save(task);

    }

    @Transactional
    public TaskDTO updateTaskStatus(Long taskId, Long taskStatusId) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException(
                        "Task not found with id: " + taskId
                ));

        TaskStatus taskStatus = taskStatusFindById(taskStatusId);

        task.setTaskStatus(taskStatus);

        Task updatedTask = taskRepository.save(task);

        return convertToDTO(updatedTask);
    }


    public TaskDTO getTask(Long taskID) {
        Task task = taskRepository.findById(taskID)
                .orElseThrow(() -> new RuntimeException(
                        "Task not found with id: " + taskID
                ));
        return convertToDTO(task);
    }

    public List<TaskDTO> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();

        return tasks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    public TaskDTO convertTaskRequestToDTO(TaskRequest taskRequest) {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setUserId(taskRequest.getUserId());
        taskDTO.setTitle(taskRequest.getTitle());
        taskDTO.setTargetUrl(taskRequest.getTargetUrl());
        taskDTO.setRequiredCompletions(taskRequest.getRequiredCompletions());
        taskDTO.setPlatformTaskTypeId(taskRequest.getPlatformTaskTypeId());

        return taskDTO;
    }

    public Task covertToEntity(TaskDTO taskDTO) {

        Task task = new Task();

        task.setUserId(getUserId(taskDTO.getUserId()));
        task.setTitle(taskDTO.getTitle());
        task.setTarget_url(taskDTO.getTargetUrl());
        task.setCredits_offered(getCredits_offered(taskDTO));
        task.setRequired_completions(taskDTO.getRequiredCompletions());
        task.setPlatformTaskType(
                platformTaskTypeFindById(taskDTO.getPlatformTaskTypeId())
        );
        task.setTaskStatus(
                taskStatusFindById(1L)
        );

        return task;
    }

    public TaskDTO convertToDTO(Task task) {

        return TaskDTO.builder()
                .id(task.getId())
                .userId(task.getUserId())
                .title(task.getTitle())
                .targetUrl(task.getTarget_url())
                .creditsOffered(task.getCredits_offered())
                .requiredCompletions(task.getRequired_completions())
                .currentCompletions(task.getCurrent_completions())
                .createdAt(task.getCreated_at())
                .expiresAt(task.getExpires_at())
                .platformTaskTypeId(task.getPlatformTaskType().getId())
                .taskStatusId(task.getTaskStatus().getId())
                .build();
    }

    public Long getUserId(Long userID) {

        UserResponse userResponse;

        try {
            userResponse = webClientBuilder.build()
                    .get()
                    .uri("http://user-service/api/user/{id}", userID)
                    .retrieve()
                    .bodyToMono(UserResponse.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            throw new RuntimeException("User not found with id: " + userID);
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

        return userResponse.getId();
    }

    public PlatformTaskType platformTaskTypeFindById(Long id) {
        return platformTaskTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "PlatformTaskType not found with id: " + id
                ));
    }

    public TaskStatus taskStatusFindById(Long id) {
        return taskStatusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "TaskStatus not found with id: " + id
                ));
    }

    public Integer getCredits_offered(TaskDTO taskDTO) {
        Long id = taskDTO.getPlatformTaskTypeId();
        PlatformTaskType platformTaskType = platformTaskTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "PlatformTaskType not found with id: " + id
                ));

        Long platformID = platformTaskType.getPlatform().getId();
        Long taskTypeID = platformTaskType.getTaskType().getId();

        Platform platform = platformRepository.findById(platformID)
                .orElseThrow(() -> new RuntimeException(
                        "Platform not found with id: " + id
                ));

        TaskType taskType = taskTypeRepository.findById(taskTypeID)
                .orElseThrow(() -> new RuntimeException(
                        "TaskType not found with id: " + id
                ));

        Integer platformCredits = platform.getCredits();
        Integer taskTypeCredits = taskType.getCredits();

        Integer creditsOffered =  platformCredits + taskTypeCredits;

        return creditsOffered;
    }

    @Transactional
    public void engagementTaskCompletion(Long taskId) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() ->
                        new RuntimeException("Task not found with id: " + taskId)
                );

        int current = task.getCurrent_completions();
        int required = task.getRequired_completions();

        // Check if already completed
        if (current >= required) {
            throw new RuntimeException("Task already completed");
        }

        // Increment current completions
        current++;
        task.setCurrent_completions(current);

        // Check AFTER increment
        if (current == required) {
            TaskStatus completedStatus = taskStatusRepository.findById(2L)
                    .orElseThrow(() -> new RuntimeException("Completed status not found"));

            task.setTaskStatus(completedStatus);
        }

        taskRepository.save(task);
    }

}

