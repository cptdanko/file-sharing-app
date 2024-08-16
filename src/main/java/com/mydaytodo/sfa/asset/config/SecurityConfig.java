package com.mydaytodo.sfa.asset.config;


import com.mydaytodo.sfa.asset.filter.JwtAuthFilter;
import com.mydaytodo.sfa.asset.service.UserAuthServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Slf4j
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter authFilter;

    // User Creation
    @Bean
    public UserDetailsService userDetailsService() {
        return UserAuthServiceImpl.instance;
    }

    // Configuring HttpSecurity
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        /*http.csrf(httpSecurityCsrfConfigurer ->
                httpSecurityCsrfConfigurer.ignoringRequestMatchers("/api/asset/upload"
                        , "/api/file/**"
                        , "/api/user/create"
                        ,"/api/user/login"));*/
        return http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/",
                                        "/static/**",
                                        "/favicon.ico",
                                        "/logo192.png",
                                        "/manifest.json",
                                        "/index.html",
                                        "/api/auth/**",
                                        "/api/auth/ping",
                                        "/api/user/create",
                                        "/index",
                                        "/ping",
                                        "/v3/api-docs/**",
                                        "/swagger-ui/**",
                                        "/healthcheck").permitAll()
                                .anyRequest()
                                .authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(Customizer.withDefaults())
                .exceptionHandling(e -> {
                        log.info("exception in logging in");
                        e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
                    }
                )
                .build();
    }
    // Password Encoding
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}