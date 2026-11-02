
package com.sreyarose.medicalshop.infrastructure.config;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sreyarose.medicalshop.domain.entity.User;
import com.sreyarose.medicalshop.domain.repository.UserRepository;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader =
                request.getHeader("Authorization");

        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7);

        try {
            if (jwtService.isTokenValid(token)
                    && SecurityContextHolder.getContext()
                            .getAuthentication() == null) {

                Claims claims = jwtService.extractClaims(token);
                String username = claims.getSubject();

                User user = userRepository.findByUsername(username)
                        .filter(User::isActive)
                        .orElse(null);

                if (user != null) {
                    var authorities = java.util.List.of(
                            new SimpleGrantedAuthority(
                                    "ROLE_" + user.getRole()));

                    var authentication =
                            new UsernamePasswordAuthenticationToken(
                                    user.getUsername(),
                                    null,
                                    authorities);

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request));

                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);
                }
            }
        } catch (io.jsonwebtoken.JwtException
                | IllegalArgumentException exception) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
