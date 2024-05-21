package co.unlearning.aicareer.global.security.oauth2;

import co.unlearning.aicareer.domain.common.user.User;
import co.unlearning.aicareer.domain.common.user.UserInterest;
import co.unlearning.aicareer.domain.common.user.UserRole;
import co.unlearning.aicareer.domain.common.user.UserTerms;
import co.unlearning.aicareer.domain.common.user.repository.UserRepository;
import co.unlearning.aicareer.domain.common.user.repository.UserTermsRepository;
import co.unlearning.aicareer.global.security.jwt.Token;
import co.unlearning.aicareer.global.security.jwt.TokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final TokenService tokenService;
    private final UserRepository userRepository;
    @Value("${front-url}")
    private String frontURL;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        Token token = tokenService.generateToken(email, "USER");

        Optional<User> userOptional = userRepository.findByEmail(email);

        if(userOptional.isEmpty()){
            User user = User.builder()
                    .email(email)
                    .name(oAuth2User.getAttribute("name"))
                    .nickname(UUID.randomUUID().toString())
                    .password("none")
                    .recommender("none")
                    .userRole(UserRole.GUEST)
                    .joinDate(LocalDateTime.now())
                    .build();
            userRepository.save(user);
            UserInterest userInterest = new UserInterest();
            userInterest.setReceiveEmail("");
            userInterest.setIsMetropolitanArea(false);
            user.setUserInterest(userInterest);

            UserTerms isMarketing = UserTerms.builder().isAgree(false).build();
            UserTerms isAgreeUseTerms = UserTerms.builder().isAgree(true).build();
            UserTerms isAgreePrivacyTerms = UserTerms.builder().isAgree(true).build();
            UserTerms isAgreeInformationTerms = UserTerms.builder().isAgree(false).build();

            user.setIsInterest(false);
            user.setIsMarketing(isMarketing);
            user.setIsAgreeUseTerms(isAgreeUseTerms);
            user.setIsAgreeInformationTerms(isAgreeInformationTerms);
            user.setIsAgreePrivacyTerms(isAgreePrivacyTerms);
            userRepository.save(user);
        }
        if(!Objects.equals(frontURL, "http://localhost:3000")) {
            ResponseCookie accessToken = ResponseCookie.from("_aT",token.getAccessToken())
                    .path("/")
                    .sameSite("None")
                    .domain(".aicareer.co.kr")
                    .httpOnly(true)
                    .secure(true)
                    .maxAge(24*60*60)
                    .build();
            response.addHeader("Set-Cookie", accessToken.toString());

            ResponseCookie refreshToken = ResponseCookie.from("_rT",token.getRefreshToken())
                    .path("/")
                    .sameSite("None")
                    .domain(".aicareer.co.kr")
                    .httpOnly(true)
                    .secure(true)
                    .maxAge(24*60*60)
                    .build();
            response.addHeader("Set-Cookie", refreshToken.toString());
            getRedirectStrategy().sendRedirect(request, response, UriComponentsBuilder.fromUriString(frontURL).queryParam("login", "true").toUriString());
        } else {
            ResponseCookie accessToken = ResponseCookie.from("_aT",token.getAccessToken())
                    .path("/")
                    .sameSite("None")
                    .domain("localhost:3000")
                    .httpOnly(true)
                    .secure(true)
                    .maxAge(24*60*60)
                    .build();
            response.addHeader("Set-Cookie", accessToken.toString());

            ResponseCookie refreshToken = ResponseCookie.from("_rT",token.getRefreshToken())
                    .path("/")
                    .sameSite("None")
                    .domain("localhost:3000")
                    .httpOnly(true)
                    .secure(true)
                    .maxAge(24*60*60)
                    .build();
            response.addHeader("Set-Cookie", refreshToken.toString());
            getRedirectStrategy().sendRedirect(request, response, UriComponentsBuilder.fromUriString(frontURL).queryParam("login", "true").toUriString());
        }
    }

    private String makeRedirectUrl(String path, String token) {
        return UriComponentsBuilder.fromUriString("http://localhost:3000")
                .path(path)
                .queryParam("token", token)
                .build().toUriString();
    }
}