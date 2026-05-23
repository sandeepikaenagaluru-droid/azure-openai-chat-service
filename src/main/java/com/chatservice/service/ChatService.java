package com.chatservice.service;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.models.*;
import com.chatservice.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatService {

    @Autowired
    private OpenAIClient openAIClient;

    @Value("${azure.openai.deployment-name}")
    private String deploymentName;

    @Value("${azure.openai.max-tokens:1000}")
    private int maxTokens;

    @Value("${azure.openai.system-prompt:You are a helpful assistant.}")
    private String systemPrompt;

    // In-memory session store: sessionId -> message history
    private final Map<String, List<ChatRequestMessage>> sessions = new ConcurrentHashMap<>();

    public ChatResponse chat(String sessionId, String userMessage) {
        // Get or create session history
        List<ChatRequestMessage> messages = sessions.computeIfAbsent(sessionId, k ->
            new ArrayList<>(List.of(
                new ChatRequestSystemMessage(systemPrompt)
            ))
        );

        // Add user message to history
        messages.add(new ChatRequestUserMessage(userMessage));

        // Call Azure OpenAI
        ChatCompletions completions = openAIClient.getChatCompletions(
            deploymentName,
            new ChatCompletionsOptions(messages)
                .setMaxTokens(maxTokens)
                .setTemperature(0.7)
        );

        String reply = completions.getChoices().get(0)
                                  .getMessage().getContent();

        // Save assistant reply to history for multi-turn context
        messages.add(new ChatRequestAssistantMessage(reply));

        int totalTokens = completions.getUsage().getTotalTokens();

        return new ChatResponse(reply, sessionId, totalTokens);
    }

    public void clearSession(String sessionId) {
        sessions.remove(sessionId);
    }

    public int getSessionMessageCount(String sessionId) {
        List<ChatRequestMessage> history = sessions.get(sessionId);
        return history == null ? 0 : history.size();
    }
}
