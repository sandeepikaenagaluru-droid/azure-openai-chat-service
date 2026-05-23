package com.yourname.chatservice.controller;

import com.yourname.chatservice.model.ChatRequest;
import com.yourname.chatservice.model.ChatResponse;
import com.yourname.chatservice.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private ChatService chatService;

    /**
     * Send a message and get a response.
     * If sessionId is null, a new session is created automatically.
     */
    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        String sessionId = (request.getSessionId() != null && !request.getSessionId().isBlank())
                ? request.getSessionId()
                : UUID.randomUUID().toString();

        ChatResponse response = chatService.chat(sessionId, request.getMessage());
        return ResponseEntity.ok(response);
    }

    /**
     * Clear the conversation history for a session.
     */
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> clearSession(@PathVariable String sessionId) {
        chatService.clearSession(sessionId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Health check endpoint.
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Azure OpenAI Chat Service is running");
    }
}
