package com.example.demo.config;

import com.example.demo.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@Slf4j
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("------------------filterChain------------------------------");
        // http 시큐리티 빌더
        // http.cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()));

        http.csrf(config -> config.disable())// csrf는 현재 사용하지 않으므로 disable
                .httpBasic(config -> config.disable())// token을 사용하므로 basic 인증 disable
                .sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS));  // session 기반이 아님을 선언

        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers("/", "/auth/**").permitAll()
                .anyRequest().authenticated());

        // filter 등록.
        // 매 요청마다
        // CorsFilter 실행한 후에
        // jwtAuthenticationFilter 실행한다.
        http.addFilterAfter(jwtAuthenticationFilter, CorsFilter.class);

        return http.build();
    }
}