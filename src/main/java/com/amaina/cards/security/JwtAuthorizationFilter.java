package com.amaina.cards.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Intercept Bearer Token request and validate if token is valid
 */
@Configuration
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    public static final String ROLE ="role";
    private static final String AUTHENTICATION_ERROR ="Authentication Error";
    public static final String MESSAGE = "message";
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            if(request.getHeader(HttpHeaders.AUTHORIZATION) !=null) {
                Claims claims = jwtUtil.decodeToken(request);
                if (claims != null) {
                    String email = claims.getSubject();
                    String role = (String) claims.get(ROLE);
                    GrantedAuthority authority = new SimpleGrantedAuthority(role);
                    Authentication authentication =
                            new JwtAuthenticationToken(email, null, List.of(authority));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }

            }finally {
            filterChain.doFilter(request, response);
        }
    }
}
