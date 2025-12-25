package com.getreviews.task_service.controller;

import com.getreviews.task_service.dto.TaskDTO;
import com.getreviews.task_service.dto.TaskRequest;
import com.getreviews.task_service.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addTask(@Valid @RequestBody TaskRequest taskRequest) {
        taskService.addTask(taskRequest);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Task added completed successfully.");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status/{statusId}")
    public ResponseEntity<Map<String, String>> updateTaskStatus(
        @PathVariable Long taskId,
        @PathVariable Long statusId)
    {
        taskService.updateTaskStatus(taskId, statusId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Task status update completed successfully.");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTask(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTask(id));
    }

    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }
}
