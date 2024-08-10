package com.mydaytodo.sfa.asset.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mydaytodo.sfa.asset.model.ServiceResponse;
import com.mydaytodo.sfa.asset.service.JwtService;
import com.mydaytodo.sfa.asset.service.UserAuthServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// This class helps us to validate the generated jwt token
@Component
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserAuthServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, ExpiredJwtException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                username = jwtService.extractUsername(token);
            }
            log.info("Username extracted from JWT token, {}", username);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
               UserDetails userDetails = userDetailsService.loadUserByUsername(username);
               log.info("Loaded userdetails object, {}", userDetails.getUsername());
                if (jwtService.validateToken(token, userDetails)) {
                    log.info("JWT token validated for expiry and about to set session context");
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    log.info("The auth token looks like, {}", authToken.toString());
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException expiredJwtException) {
            log.info("In the expired JWT Exception handler");
            response.setContentType("application/json");
            response.setStatus(HttpStatus.FORBIDDEN.value());
            ObjectMapper mapper = new ObjectMapper();
            String exMessage = mapper.writeValueAsString(expiredJwtException(expiredJwtException.getMessage()));
            response.getWriter().write(exMessage);
        }
    }
    private ServiceResponse expiredJwtException(String message) {
        return ServiceResponse.builder()
                .data("Token expired, please logout and login again")
                .message(message)
                .status(HttpStatus.FORBIDDEN.value())
                .build();
    }
}
