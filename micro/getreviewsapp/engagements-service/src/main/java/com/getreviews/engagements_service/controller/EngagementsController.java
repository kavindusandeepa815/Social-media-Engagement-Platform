package com.getreviews.engagements_service.controller;

import com.getreviews.engagements_service.dto.EngagementsDTO;
import com.getreviews.engagements_service.dto.EngagementsRequestDTO;
import com.getreviews.engagements_service.service.EngagementsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/engagements")
@RequiredArgsConstructor
public class EngagementsController {

    private final EngagementsService engagementsService;

    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addEngagements(@Valid @RequestBody EngagementsRequestDTO dto) {
        engagementsService.addEngagements(dto);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Engagements added completed successfully.");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status/{statusId}")
    public ResponseEntity<Map<String, String>> updateStatus(
            @PathVariable Long id,
            @PathVariable Long statusId) {
        engagementsService.updateEngagementsStatus(id, statusId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Engagements status update completed successfully.");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EngagementsDTO> getEngagementById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                engagementsService.getEngagementsById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<EngagementsDTO>> getAllEngagements() {
        return ResponseEntity.ok(
                engagementsService.getAllEngagements()
        );
    }
}
