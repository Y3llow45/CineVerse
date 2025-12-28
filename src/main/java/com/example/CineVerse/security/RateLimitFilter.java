package com.example.CineVerse.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();
        String ip = request.getRemoteAddr();

        Bandwidth limit = resolveLimit(path);
        if (limit == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String key = ip + ":" + path;
        Bucket bucket = buckets.computeIfAbsent(key,
                k -> Bucket.builder().addLimit(limit).build()
        );

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(429);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("""
                {"error":"Too many requests. Please try again later."}
            """);
        }
    }

    private Bandwidth resolveLimit(String path) {

        if (path.equals("/auth/login")) {
            return Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(1)));
        }

        if (path.equals("/auth/register")) {
            return Bandwidth.classic(3, Refill.greedy(3, Duration.ofMinutes(1)));
        }

        if (path.equals("/auth/2fa/confirm")) {
            return Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(1)));
        }

        if (path.equals("/auth/2fa/setup")) {
            return Bandwidth.classic(3, Refill.greedy(3, Duration.ofMinutes(1)));
        }

        return null; // no rate limit
    }
}
