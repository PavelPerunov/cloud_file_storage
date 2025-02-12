package com.perunovpavel.cloud_file_storage.config.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.perunovpavel.cloud_file_storage.config.CustomAuthenticationEntryPoint;
import com.perunovpavel.cloud_file_storage.config.CustomUserDetailsService;
import com.perunovpavel.cloud_file_storage.model.dto.ErrorResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        auth -> auth
                                .requestMatchers("/v3/api-docs",
                                        "/v3/api-docs/**",
                                        "/v3/api-docs.yaml",
                                        "/swagger-ui.html",
                                        "/swagger-ui/**").permitAll()
                                .requestMatchers("/api/v1/registration",
                                        "/api/v1/login").permitAll()
                                .requestMatchers("/api/v1/admin**").hasRole("ADMIN")
                                .anyRequest().authenticated())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint()))
                .logout(
                        logout -> logout
                                .logoutUrl("/api/v1/auth/logout")
                                .logoutSuccessHandler((request, response, authentication) -> {
                                            if (authentication == null || !authentication.isAuthenticated()) {
                                                String jsonErrorMessage = getJsonErrorMessage();
                                                response.setContentType("application/json");
                                                response.setCharacterEncoding("UTF-8");
                                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                                response.getWriter().write(jsonErrorMessage);
                                                return;
                                            }
                                            response.setCharacterEncoding("UTF-8");
                                            response.setStatus(HttpServletResponse.SC_OK);
                                            response.getWriter().write("Logout successful!");
                                        }
                                )
                                .invalidateHttpSession(true))
                .requestCache(RequestCacheConfigurer::disable)
                .build();
    }

    private static String getJsonErrorMessage() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(ErrorResponseDto.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message("The user is not authorized to perform logout")
                .build());
    }

    @Bean
    public CustomUserDetailsService customUserDetailsService() {
        return new CustomUserDetailsService();
    }

    @Bean
    public CustomAuthenticationEntryPoint customAuthenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }


    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder.userDetailsService(customUserDetailsService())
                .passwordEncoder(passwordEncoder());

        return authenticationManagerBuilder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
