package co.unlearning.aicareer.global.security.oauth2;

import co.unlearning.aicareer.domain.user.User;
import co.unlearning.aicareer.domain.user.UserRole;
import co.unlearning.aicareer.domain.user.repository.UserRepository;
import co.unlearning.aicareer.global.security.jwt.Token;
import co.unlearning.aicareer.global.security.jwt.TokenService;
import jakarta.persistence.Column;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final TokenService tokenService;
    private final UserRepository userRepository;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        Token token = tokenService.generateToken(email, "USER");

        Optional<User> userOptional = userRepository.findByEmail(email);

        String url;
        //최초 로그인 시 회원가입
        if(userOptional.isEmpty()){
            userRepository.save(User.builder()
                    .email(email)
                    .name(oAuth2User.getAttribute("name"))
                            .nickname(UUID.randomUUID().toString())
                            .password("none")
                            .recommender("none")
                            .userRole(UserRole.USER)
                            .joinDate(LocalDateTime.now())
                    .build());
        }
        ResponseCookie accessToken = ResponseCookie.from("_aT",token.getAccessToken())
                .path("/")
                .sameSite("None")
                .domain("aicareer.co.kr")
                .httpOnly(true)
                .secure(true)
                .maxAge(24*60*60)
                .build();
        response.addHeader("Set-Cookie", accessToken.toString());

        ResponseCookie refreshToken = ResponseCookie.from("_rT",token.getRefreshToken())
                .path("/")
                .sameSite("None")
                .domain("aicareer.co.kr")
                .httpOnly(true)
                .secure(true)
                .maxAge(24*60*60)
                .build();
        response.addHeader("Set-Cookie", refreshToken.toString());
        getRedirectStrategy().sendRedirect(request, response, UriComponentsBuilder.fromUriString("https://aicareer.co.kr").queryParam("login", "true").toUriString());
    }

    private String makeRedirectUrl(String path, String token) {
        return UriComponentsBuilder.fromUriString("http://localhost:3000")
                .path(path)
                .queryParam("token", token)
                .build().toUriString();
    }
}