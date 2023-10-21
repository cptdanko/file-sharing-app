package com.mydaytodo.sfa.asset.config;

import com.mydaytodo.sfa.asset.repository.UserRepositoryImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
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
@Slf4j
public class SecurityConfig {

    @Autowired
    private UserRepositoryImpl userRepository;

    // @Bean
    /*public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }*/
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
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
        return httpSecurity.build();
    }
    /*@Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(bCryptPasswordEncoder());
    }*/

    /**
     * A hack, please improve this
     * disabling this method below because http POST,
     * DELETE and PUT endpoints were not working and
     * only GET was working
     * @return
     */
    /*@Bean
    public InMemoryUserDetailsManager userDetailsManager() {
        List<AssetUser> users = userRepository.getAllUsers();

        UserDetails[] udArr = new UserDetails[users.size()];
        int count = 0;
        log.info("UserDetails Array Size is "+ udArr.length);
        for(AssetUser au: users) {
            UserDetails u1 = User.builder()
                    .username(au.getUsername())
                    .password(bCryptPasswordEncoder().encode(au.getPassword()))
                    .roles("ADMIN")
                    .build();
            udArr[count] = u1;
            count += 1;
        }
        return new InMemoryUserDetailsManager(udArr);
    }*/
}
