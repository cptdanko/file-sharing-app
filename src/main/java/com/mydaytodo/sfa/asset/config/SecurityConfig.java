package com.mydaytodo.sfa.asset.config;

import com.mydaytodo.sfa.asset.service.UserAuthServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static jakarta.servlet.DispatcherType.ERROR;
import static jakarta.servlet.DispatcherType.FORWARD;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> {
                    try {
                        auth.dispatcherTypeMatchers(FORWARD, ERROR).permitAll();
                        auth.requestMatchers("/",
                                        "/static/**",
                                        "/favicon.ico",
                                        "/logo192.png",
                                        "/manifest.json",
                                        "/index.html",
                                        "/api/user/login",
                                        "/api/user/",
                                        "/index",
                                        "/ping",
                                        "/healthcheck").permitAll()
                                .anyRequest()
                                .authenticated();
                    } catch (RuntimeException re) {
                        throw new RuntimeException("Error in the security config ->" + re.getMessage());
                    }
                })
                .csrf(httpSecurityCsrfConfigurer ->
                        httpSecurityCsrfConfigurer.ignoringRequestMatchers("/api/asset/upload"
                                , "/api/file/**"
                                , "/api/user/**"));

        return httpSecurity.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager inMemoryUserDetailsManager = UserAuthServiceImpl.instance.getInMemoryUserDetailsManager();
        UserDetails userDetails = User.withUsername("bhuman")
                .password(bCryptPasswordEncoder().encode("password"))
                .roles("ADMIN", "USER")
                .build();
        inMemoryUserDetailsManager.createUser(userDetails);
        ;
        return inMemoryUserDetailsManager;
    }
}