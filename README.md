Azure OpenAI Chat Service
A production-ready Java Spring Boot REST API that integrates with Azure OpenAI to provide multi-turn conversational AI with built-in rate limiting.
Features
Multi-turn conversations with session management
Azure OpenAI GPT-4o integration via Azure SDK
Per-IP rate limiting (10 requests/minute) using Bucket4j
Standard `X-RateLimit-*` response headers
RESTful API with session info and clear endpoints
Environment-variable-based config (safe for open source)
Tech Stack
Layer	Technology
Framework	Spring Boot 3.2
Language	Java 17
AI Service	Azure OpenAI (GPT-4o)
Rate Limiting	Bucket4j
Build	Maven
Prerequisites
Java 17+
Maven 3.8+
Azure account with OpenAI resource created
GPT-4o model deployed in your Azure OpenAI resource
Setup
1. Clone the repo
```bash
git clone https://github.com/YOUR_USERNAME/azure-openai-chat-service.git
cd azure-openai-chat-service
```
2. Set environment variables
```bash
export AZURE_OPENAI_ENDPOINT=https://your-resource.openai.azure.com/
export AZURE_OPENAI_API_KEY=your-api-key-here
export AZURE_OPENAI_DEPLOYMENT_NAME=gpt-4o
```
3. Run the application
```bash
mvn spring-boot:run
```
Server starts at `http://localhost:8080`
API Reference
POST /api/chat
Send a message. Omit `sessionId` to start a new conversation.
Request:
```json
{
  "sessionId": "optional-existing-session-id",
  "message": "What is Java?"
}
```
Response:
```json
{
  "reply": "Java is a high-level, object-oriented programming language...",
  "sessionId": "a1b2c3d4-...",
  "totalTokens": 142
}
```
DELETE /api/chat/{sessionId}
Clear conversation history for a session.
GET /api/chat/{sessionId}/info
Get message count for a session.
Rate Limiting
Each IP address is limited to 10 requests per minute.
Exceeded requests return HTTP `429 Too Many Requests`:
```json
{
  "error": "Too many requests",
  "message": "Rate limit exceeded. Max 10 requests per minute.",
  "retryAfter": 60
}
```
Every response includes headers:
```
X-RateLimit-Limit: 10
X-RateLimit-Remaining: 7
X-RateLimit-Window: 60s
```
Test with curl
```bash
# New conversation
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello! What is Java?"}'

# Continue same session
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"sessionId": "YOUR_SESSION_ID", "message": "Give me a code example"}'

# Clear session
curl -X DELETE http://localhost:8080/api/chat/YOUR_SESSION_ID
```
Configuration
All settings in `application.yml` are driven by environment variables:
Property	Env Variable	Default
Azure endpoint	`AZURE_OPENAI_ENDPOINT`	required
Azure API key	`AZURE_OPENAI_API_KEY`	required
Deployment name	`AZURE_OPENAI_DEPLOYMENT_NAME`	`gpt-4o`
Max tokens	—	`1000`
Rate limit	—	`10/min`
Project Structure
```
src/main/java/com/chatservice/
├── ChatServiceApplication.java
├── controller/
│   └── ChatController.java
├── service/
│   ├── ChatService.java
│   └── RateLimiterService.java
├── interceptor/
│   └── RateLimitInterceptor.java
├── config/
│   ├── AzureOpenAIConfig.java
│   └── WebConfig.java
└── model/
    ├── ChatRequest.java
    └── ChatResponse.java
```
License
MIT
