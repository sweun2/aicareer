package co.unlearning.aicareer.domain.user.controller;

import co.unlearning.aicareer.domain.user.User;
import co.unlearning.aicareer.domain.user.dto.UserRequestDto;
import co.unlearning.aicareer.domain.user.dto.UserResponseDto;
import co.unlearning.aicareer.domain.user.repository.UserRepository;
import co.unlearning.aicareer.domain.user.service.UserService;
import co.unlearning.aicareer.global.security.jwt.Token;
import co.unlearning.aicareer.global.security.jwt.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Tag(name = "user", description = "유저 api")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    /*@Operation(summary = "선생님 로그인하기", description = "선생님을 로그인 합니다. 쿠키로 accessToken이 갑니다.")
    @PostMapping("/login")
    public ResponseEntity<UserResponseDto.Simple> Login(@RequestBody UserRequestDto.LoginForm loginForm) {
        User user = User.builder()
                .phone(loginForm.getPhone())
                .password(loginForm.getPassword())
                .build();

        if (userService.verifyLoginUser(user)){
            Token token = tokenService.generateToken(user.getPhone(), "USER");

            //samesite 재설정 필요
            ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", token.getAccessToken())
                    .maxAge(24 * 60 * 60)
                    .path("/")
                    .httpOnly(true)
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Set-Cookie", accessTokenCookie.toString());

            ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", token.getRefreshToken())
                    .maxAge(7 * 24 * 60 * 60)
                    .path("/")
                    .httpOnly(true)
                    .build();

            headers.add("Set-Cookie", refreshTokenCookie.toString());


            log.info(accessTokenCookie.toString());
            log.info(refreshTokenCookie.toString());

            User serverUser = userRepository.findByPhone(user.getPhone()).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다.")
            );
            return ResponseEntity.status(HttpStatus.OK).headers(headers).body(UserResponseDto.Simple.of(serverUser));

        }
        else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"로그인 실패");
    }*/

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "유저 정보 가져오기", description = "현재 로그인된 유저 정보를 가져옵니다.")
    @GetMapping("/info")
    public ResponseEntity<UserResponseDto.Simple> userInfo() {
        return ResponseEntity.ok(UserResponseDto.Simple.of(userService.getLoginUser()));
    }
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "로그아웃", description = "로그아웃하기 accessToken, refresh token 둘다 덮어쓰기")
    @GetMapping("/logout")
    public ResponseEntity<String> Login() {
        User user = userService.getLoginUser();
        if (userService.verifyLoginUser(user)){
            Token token = tokenService.generateToken(user.getEmail(), "USER");

            //samesite 재설정 필요
            ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", token.getAccessToken())
                    .maxAge(0)
                    .path("/")
                    .httpOnly(true)
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Set-Cookie", accessTokenCookie.toString());

            ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", token.getRefreshToken())
                    .maxAge(0)
                    .path("/")
                    .httpOnly(true)
                    .build();

            headers.add("Set-Cookie", refreshTokenCookie.toString());


            log.info(accessTokenCookie.toString());
            log.info(refreshTokenCookie.toString());
            return ResponseEntity.status(HttpStatus.OK).headers(headers).body("logout");

        }
        else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"로그인 실패");
    }
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "유저 Role 변경", description = "현재 로그인된 유저의 Role을 변경합니다. Role이 ADMIN인 경우만 사용 가능, ADMIN으로의 변경은 DB에서 직접 변경")
    @PostMapping("/role")
    public ResponseEntity<UserResponseDto.Info> userInfo(UserRequestDto.UserRole userRole) {
        return ResponseEntity.ok(UserResponseDto.Info.of(userService.updateUserRole(userRole)));
    }
}