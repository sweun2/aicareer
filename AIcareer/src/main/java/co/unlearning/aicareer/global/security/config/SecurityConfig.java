package co.unlearning.aicareer.global.security.config;


import co.unlearning.aicareer.domain.user.service.UserService;
import co.unlearning.aicareer.global.security.jwt.JwtAuthFilter;
import co.unlearning.aicareer.global.security.jwt.TokenService;
import co.unlearning.aicareer.global.security.oauth2.CustomOAuth2UserService;
import co.unlearning.aicareer.global.security.oauth2.OAuth2SuccessHandler;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collections;

import static java.util.List.of;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig implements WebMvcConfigurer {
    private final TokenService tokenService;
    private final UserService userService;
    private final CustomOAuth2UserService oAuth2Service;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedHeaders(Collections.singletonList("*"));
            config.setAllowedMethods(Collections.singletonList("*"));
            config.setAllowedOriginPatterns(Collections.singletonList("*")); //
            config.setAllowCredentials(true);

            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", config);

            return config;
        };
    }
    @Bean
    public SecurityFilterChain filterChain(final @NotNull HttpSecurity http) throws Exception{
        http.httpBasic(HttpBasicConfigurer::disable)
                .csrf(CsrfConfigurer::disable)
                .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()))
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize ->
                        authorize
                                .anyRequest().permitAll()
                )
                .oauth2Login(oauth2 -> {
                    oauth2.loginPage("/token/expired")
                            .successHandler(oAuth2SuccessHandler)
                            .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2Service));
                })
                /*.oauth2Login(oauth2 -> {
                    oauth2.successHandler(oAuth2SuccessHandler)
                            .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2Service))
                            .loginPage("/token/expired");
                })*/
                .addFilterBefore(new JwtAuthFilter(tokenService, userService), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
