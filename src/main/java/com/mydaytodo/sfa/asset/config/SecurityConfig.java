package com.mydaytodo.sfa.asset.config;

import com.mydaytodo.sfa.asset.security.CustomUserDetailsService;
import com.mydaytodo.sfa.asset.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static jakarta.servlet.DispatcherType.ERROR;
import static jakarta.servlet.DispatcherType.FORWARD;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    /*@Autowired
    private CustomUserDetailsService userDetailsService;*/
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        //authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder());
        //authenticationManager = authenticationManagerBuilder.build();
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
                        authorizationManagerRequestMatcherRegistry
                                .dispatcherTypeMatchers(FORWARD, ERROR).permitAll()
                                .requestMatchers(
                                        antMatcher("/index.html"),
                                        antMatcher("/"),
                                        antMatcher("/login"),
                                        antMatcher("/auth/**")).permitAll()
                                .anyRequest()
                                .authenticated())
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }
    /*@Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(bCryptPasswordEncoder());
    }*/
}
