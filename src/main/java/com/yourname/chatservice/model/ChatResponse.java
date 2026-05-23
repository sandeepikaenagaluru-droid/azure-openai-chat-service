package com.yourname.chatservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatResponse {
    private String reply;
    private String sessionId;
    private int totalTokens;
}
