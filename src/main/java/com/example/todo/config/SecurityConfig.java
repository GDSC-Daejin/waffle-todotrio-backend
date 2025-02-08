    package com.example.todo.config;

    import com.example.todo.jwt.JwtFilter;
    import com.example.todo.jwt.TokenProvider;
    import com.example.todo.service.OAuth2UserService;
    import com.example.todo.service.UserService;
    import lombok.RequiredArgsConstructor;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.config.http.SessionCreationPolicy;
    import org.springframework.security.core.userdetails.UserDetailsService;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.security.web.SecurityFilterChain;
    import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

    import org.springframework.web.cors.CorsConfiguration;
    import org.springframework.web.cors.CorsConfigurationSource;

    import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
    import java.util.Arrays;
    import java.util.List;


    @Configuration
    @EnableWebSecurity
    @RequiredArgsConstructor
    public class SecurityConfig {

        private final TokenProvider tokenProvider;
        private final UserDetailsService userDetailsService;

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
            CorsConfiguration configuration = new CorsConfiguration();

            configuration.setAllowedOriginPatterns(List.of(
                    "http://localhost:3001",
                    "http://localhost:8080",
                    "http://0.0.0.0:3001",
                    "http://0.0.0.0:3000",
                    "http://0.0.0.0:8080",
                    "*"  // 모든 origin 허용 (개발 시에만 사용)
            ));
            configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));  // 모든 HTTP 메소드 명시적 허용
            configuration.setAllowedHeaders(List.of("*"));  // 모든 헤더 허용
            configuration.setExposedHeaders(List.of("*"));  // 모든 응답 헤더 노출
            configuration.setAllowCredentials(false);
            configuration.setMaxAge(3600L);  // preflight 캐시 시간

            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", configuration);
            return source;
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(csrf -> csrf.disable())
                    .cors(cors -> cors.configurationSource(corsConfigurationSource()))  // cors() -> disable() 대신 설정 적용
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/api/auth/**",
                                    "/swagger-ui/**",
                                    "/swagger-ui.html",
                                    "/v3/api-docs/**",
                                    "/swagger-resources/**",
                                    "/webjars/**",
                                    "/**"
                                    ).permitAll()
                            .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
                            .anyRequest().authenticated()
                    )
                    .sessionManagement(session -> session
                            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    )
                    .addFilterBefore(new JwtFilter(tokenProvider, userDetailsService),
                            UsernamePasswordAuthenticationFilter.class)
                    .formLogin(form -> form.disable())
                    .httpBasic(basic -> basic.disable());

            return http.build();
        }

        @Bean
        public BCryptPasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }