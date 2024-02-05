package co.unlearning.aicareer.global.security.jwt;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;


@RequiredArgsConstructor
@RestController
@Tag(name = "token", description = "access token 발급")
@RequestMapping("/api")
public class TokenController {

    private final TokenService tokenService;

    //access 토큰 만료시 refresh 토큰을 통해 재발급
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/token/refresh")
    public ResponseEntity<String> refreshAuth(HttpServletRequest request) {
        Token newToken = tokenService.refresh(request);

        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", newToken.getAccessToken())
                .maxAge(24 * 60 * 60)
                .path("/")
                .httpOnly(true)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Set-Cookie", accessTokenCookie.toString());

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(accessTokenCookie.toString());
    }
    @GetMapping("/token/expired")
    public ResponseEntity<String> logout() {
        HttpHeaders headers = new HttpHeaders();
        ResponseCookie accessToken = ResponseCookie.from("accessToken","")
                .path("/")
                .sameSite("None")
                .domain("aicareer.co.kr")
                .httpOnly(true)
                .secure(true)
                .maxAge(24*60*60)
                .build();

        headers.set("Set-Cookie", accessToken.toString());

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(accessToken.toString());
    }
    /*@GetMapping("/token/expired")
    public void logout(HttpServletResponse response,HttpServletRequest request) throws IOException {
        ResponseCookie accessToken = ResponseCookie.from("accessToken","")
                .path("/")
                .sameSite("None")
                .domain("aicareer.co.kr")
                .httpOnly(true)
                .secure(true)
                .maxAge(24*60*60)
                .build();
        response.addHeader("Set-Cookie", accessToken.toString());

        RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
        redirectStrategy.sendRedirect(request, response, "https://aicareer.co.kr?login=true");
    }*/

}
