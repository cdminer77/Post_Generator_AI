package com.example.postgenerator.controller;

import com.example.postgenerator.model.PostGenerationRequest;
import com.example.postgenerator.model.PostGenerationResponse;
import com.example.postgenerator.service.PostOrchestratorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostApiController {

    private final PostOrchestratorService orchestratorService;

    @PostMapping("/generate")
    public ResponseEntity<PostGenerationResponse> generatePost(@Valid @RequestBody PostGenerationRequest request) {
        PostGenerationResponse response = orchestratorService.processWebsiteAndGeneratePost(request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
