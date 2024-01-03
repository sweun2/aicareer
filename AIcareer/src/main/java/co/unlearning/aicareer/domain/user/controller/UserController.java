package co.unlearning.aicareer.domain.user.controller;

import co.unlearning.aicareer.domain.recruitment.dto.RecruitmentResponseDto;
import co.unlearning.aicareer.domain.user.User;
import co.unlearning.aicareer.domain.user.dto.UserRequestDto;
import co.unlearning.aicareer.domain.user.dto.UserResponseDto;
import co.unlearning.aicareer.domain.user.repository.UserRepository;
import co.unlearning.aicareer.domain.user.service.UserService;
import co.unlearning.aicareer.global.security.jwt.Token;
import co.unlearning.aicareer.global.security.jwt.TokenService;
import co.unlearning.aicareer.global.utils.error.ApiErrorCodeExample;
import co.unlearning.aicareer.global.utils.error.ApiErrorCodeExamples;
import co.unlearning.aicareer.global.utils.error.code.RecruitmentErrorCode;
import co.unlearning.aicareer.global.utils.error.code.UserErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "유저의 기본 정보 가져오기", description = "현재 로그인된 유저의 기본 정보를 가져옵니다.")
    @ApiResponse(
            responseCode = "200",
            description = "정상 응답",
            content = @Content(
                    schema = @Schema(implementation = UserResponseDto.Simple.class)))
    @ApiErrorCodeExample(UserErrorCode.class)
    @GetMapping("/simple")
    public ResponseEntity<UserResponseDto.Simple> findUserSimple() {
        return ResponseEntity.ok(UserResponseDto.Simple.of(userService.getLoginUser()));
    }
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "유저의 모든 정보 가져오기", description = "현재 로그인된 유저의 모든 정보를 가져옵니다.")
    @ApiResponse(
            responseCode = "200",
            description = "정상 응답",
            content = @Content(
                    schema = @Schema(implementation = UserResponseDto.Info.class)))
    @ApiErrorCodeExample(UserErrorCode.class)
    @GetMapping("/info")
    public ResponseEntity<UserResponseDto.Info> findUserInfo() {
        return ResponseEntity.ok(UserResponseDto.Info.of(userService.getLoginUser()));
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "유저 Role 변경", description = "현재 로그인된 유저의 Role을 변경합니다. Role이 ADMIN인 경우만 사용 가능, ADMIN으로의 변경은 DB에서 직접 변경")
    @ApiErrorCodeExample(UserErrorCode.class)
    @PostMapping("/role")
    public ResponseEntity<UserResponseDto.Info> userInfo(UserRequestDto.UserRole userRole) {
        return ResponseEntity.ok(UserResponseDto.Info.of(userService.updateUserRole(userRole)));
    }
}