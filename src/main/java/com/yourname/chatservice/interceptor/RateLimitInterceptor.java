package com.yourname.chatservice.interceptor;

import com.yourname.chatservice.service.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private RateLimiterService rateLimiterService;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String clientIp = getClientIp(request);
        boolean allowed = rateLimiterService.tryConsume(clientIp);

        if (!allowed) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("""
                {
                  "error": "Too many requests",
                  "message": "Rate limit exceeded. Max 10 requests per minute.",
                  "retryAfter": 60
                }
            """);
            return false;
        }

        // Add standard rate limit headers
        long remaining = rateLimiterService.getRemainingTokens(clientIp);
        response.addHeader("X-RateLimit-Limit", "10");
        response.addHeader("X-RateLimit-Remaining", String.valueOf(remaining));
        response.addHeader("X-RateLimit-Window", "60s");

        return true;
    }

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
