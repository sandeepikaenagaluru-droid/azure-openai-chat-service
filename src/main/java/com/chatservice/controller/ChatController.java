package com.chatservice.controller;

import com.chatservice.model.ChatRequest;
import com.chatservice.model.ChatResponse;
import com.chatservice.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private ChatService chatService;

    /**
     * Send a message. If sessionId is null, a new session is created.
     * POST /api/chat
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
     * Clear conversation history for a session.
     * DELETE /api/chat/{sessionId}
     */
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Map<String, String>> clearSession(@PathVariable String sessionId) {
        chatService.clearSession(sessionId);
        return ResponseEntity.ok(Map.of(
            "message", "Session cleared",
            "sessionId", sessionId
        ));
    }

    /**
     * Get message count for a session.
     * GET /api/chat/{sessionId}/info
     */
    @GetMapping("/{sessionId}/info")
    public ResponseEntity<Map<String, Object>> sessionInfo(@PathVariable String sessionId) {
        int count = chatService.getSessionMessageCount(sessionId);
        return ResponseEntity.ok(Map.of(
            "sessionId", sessionId,
            "messageCount", count
        ));
    }
}
