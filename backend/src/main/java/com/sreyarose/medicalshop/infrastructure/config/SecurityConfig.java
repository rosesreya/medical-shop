
package com.sreyarose.medicalshop.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .authorizeHttpRequests(auth -> auth

                // Only POST login is public
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/auth/login"
                ).permitAll()

                // ADMIN and STAFF can view master records
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/categories",
                    "/api/categories/**",
                    "/api/manufacturers",
                    "/api/manufacturers/**",
                    "/api/generic-names",
                    "/api/generic-names/**",
                    "/api/products",
                    "/api/products/**"
                ).hasAnyRole("ADMIN", "STAFF")

                // Only ADMIN can create master records
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/categories",
                    "/api/categories/**",
                    "/api/manufacturers",
                    "/api/manufacturers/**",
                    "/api/generic-names",
                    "/api/generic-names/**",
                    "/api/products",
                    "/api/products/**"
                ).hasRole("ADMIN")

                // Only ADMIN can modify master records
                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/categories",
                    "/api/categories/**",
                    "/api/manufacturers",
                    "/api/manufacturers/**",
                    "/api/generic-names",
                    "/api/generic-names/**",
                    "/api/products",
                    "/api/products/**"
                ).hasRole("ADMIN")

                .requestMatchers(
                    HttpMethod.PATCH,
                    "/api/categories",
                    "/api/categories/**",
                    "/api/manufacturers",
                    "/api/manufacturers/**",
                    "/api/generic-names",
                    "/api/generic-names/**",
                    "/api/products",
                    "/api/products/**"
                ).hasRole("ADMIN")

                .requestMatchers(
                    HttpMethod.DELETE,
                    "/api/categories",
                    "/api/categories/**",
                    "/api/manufacturers",
                    "/api/manufacturers/**",
                    "/api/generic-names",
                    "/api/generic-names/**",
                    "/api/products",
                    "/api/products/**"
                ).hasRole("ADMIN")

                // All user-management operations are ADMIN-only
                .requestMatchers("/api/users", "/api/users/**")
                .hasRole("ADMIN")

                .requestMatchers(
                HttpMethod.GET,
                "/api/batches/**"
                ).hasAnyRole("ADMIN", "STAFF")

                .requestMatchers(
                HttpMethod.GET,
                "/api/suppliers",
                "/api/suppliers/**"
                ).hasAnyRole("ADMIN", "STAFF")

                .requestMatchers(
                 HttpMethod.POST,
                 "/api/suppliers",
                "/api/suppliers/**"
                ).hasRole("ADMIN")

                .requestMatchers(
                    HttpMethod.PATCH,
                    "/api/suppliers/**"
                ).hasRole("ADMIN")
                .requestMatchers(
            HttpMethod.POST,
            "/api/purchases",
            "/api/purchases/**"
        ).hasAnyRole("ADMIN", "STAFF")
        .requestMatchers(
            HttpMethod.GET,
            "/api/purchases",
            "/api/purchases/**"
        ).hasAnyRole("ADMIN", "STAFF")
        .requestMatchers(
            HttpMethod.GET,
            "/api/batches",
            "/api/batches/**"
        ).hasAnyRole("ADMIN", "STAFF")
        .requestMatchers(HttpMethod.POST, "/api/bills")
.hasAnyRole("ADMIN", "STAFF")

.requestMatchers(HttpMethod.GET, "/api/bills", "/api/bills/**")
.hasAnyRole("ADMIN", "STAFF")

                // Deny all routes without an explicit access policy
                .anyRequest().denyAll()
            )

            .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) ->
                    response.sendError(
                        jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED
                    )
                )
            )

            .httpBasic(basic -> basic.disable())
            .formLogin(form -> form.disable())
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}
