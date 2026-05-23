package com.yourname.chatservice.model;

import lombok.Data;

@Data
public class ChatRequest {
    private String sessionId;  // null = new session
    private String message;
}
