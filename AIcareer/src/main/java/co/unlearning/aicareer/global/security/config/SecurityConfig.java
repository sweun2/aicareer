package co.unlearning.aicareer.global.security.config;


import co.unlearning.aicareer.domain.user.service.UserService;
import co.unlearning.aicareer.global.security.jwt.JwtAuthFilter;
import co.unlearning.aicareer.global.security.jwt.TokenService;
import co.unlearning.aicareer.global.security.oauth2.CustomOAuth2UserService;
import co.unlearning.aicareer.global.security.oauth2.OAuth2SuccessHandler;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.List.of;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final TokenService tokenService;
    private final UserService userService;
    private final CustomOAuth2UserService oAuth2Service;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedHeaders(List.of("*"));
        config.setMaxAge(3600L);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        List<String> origins = new ArrayList<>();

        origins.add("https://www.aicareer.co.kr");  // 추가 도메인
        origins.add("https://aicareer.co.kr");
        origins.add("https://api.aicareer.co.kr");
        origins.add("https://localhost:8080");
        origins.add("http://localhost:8080");

        config.setAllowedOrigins(origins);
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(final @NotNull HttpSecurity http) throws Exception {
        http
                .httpBasic(HttpBasicConfigurer::disable)
                .csrf(CsrfConfigurer::disable)
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors((configurer)->corsConfigurationSource())
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                                .anyRequest().permitAll()
                )
                .oauth2Login(oauth2 -> {
                    oauth2.loginPage("/token/expired")
                            .successHandler(oAuth2SuccessHandler)
                            .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2Service));
                })
                .addFilterBefore(new JwtAuthFilter(tokenService, userService), UsernamePasswordAuthenticationFilter.class);
        http
                .logout(logout ->
                        logout
                                .logoutUrl("/api/user/logout") // 로그아웃 URL 설정 (기본값은 "/logout")
                                .logoutSuccessUrl("/api/token/expired") // 로그아웃 성공 시 이동할 URL 설정
                                .deleteCookies("JSESSIONID", "_aT") // 로그아웃 시 삭제할 쿠키 설정
                                .permitAll() // 로그아웃 URL은 모든 사용자에게 허용
                );
        return http.build();
    }
}