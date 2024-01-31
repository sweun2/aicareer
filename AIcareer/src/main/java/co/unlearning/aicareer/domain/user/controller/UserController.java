package co.unlearning.aicareer.domain.user.controller;

import co.unlearning.aicareer.domain.user.dto.UserRequestDto;
import co.unlearning.aicareer.domain.user.dto.UserResponseDto;
import co.unlearning.aicareer.domain.user.service.UserService;
import co.unlearning.aicareer.global.utils.error.ApiErrorCodeExample;
import co.unlearning.aicareer.global.utils.error.ApiErrorCodeExamples;
import co.unlearning.aicareer.global.utils.error.code.ResponseErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            useReturnTypeSchema = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResponseDto.UserSimple.class)
            ))
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(ResponseErrorCode.INTERNAL_SERVER_ERROR),
            @ApiErrorCodeExample(ResponseErrorCode.INVALID_DATE_STRING_INPUT),
            @ApiErrorCodeExample(ResponseErrorCode.USER_UNAUTHORIZED),
            @ApiErrorCodeExample(ResponseErrorCode.USER_NOT_FOUND),
    })
    @GetMapping("/simple")
    public ResponseEntity<UserResponseDto.UserSimple> findUserSimple() {
        return ResponseEntity.ok(UserResponseDto.UserSimple.of(userService.getLoginUser()));
    }
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "유저의 모든 정보 가져오기", description = "현재 로그인된 유저의 모든 정보를 가져옵니다.")
    @ApiResponse(
            responseCode = "200",
            description = "정상 응답",
            content = @Content(
                    schema = @Schema(implementation = UserResponseDto.UserInfo.class)))
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(ResponseErrorCode.INTERNAL_SERVER_ERROR),
            @ApiErrorCodeExample(ResponseErrorCode.INVALID_DATE_STRING_INPUT),
            @ApiErrorCodeExample(ResponseErrorCode.USER_UNAUTHORIZED),
            @ApiErrorCodeExample(ResponseErrorCode.USER_NOT_FOUND),
    })
    @GetMapping("/info")
    public ResponseEntity<UserResponseDto.UserInfo> findUserInfo() {
        log.info("/info");
        return ResponseEntity.ok(UserResponseDto.UserInfo.of(userService.getLoginUser()));
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "유저 Role 변경", description = "현재 로그인된 유저의 Role을 변경합니다. Role이 ADMIN인 경우만 사용 가능, ADMIN으로의 변경은 DB에서 직접 변경 필요, 문의바람")
    @ApiResponse(
            responseCode = "200",
            description = "정상 응답",
            content = @Content(
                    schema = @Schema(implementation = UserResponseDto.UserInfo.class)))
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(ResponseErrorCode.INTERNAL_SERVER_ERROR),
            @ApiErrorCodeExample(ResponseErrorCode.INVALID_DATE_STRING_INPUT),
            @ApiErrorCodeExample(ResponseErrorCode.USER_UNAUTHORIZED),
            @ApiErrorCodeExample(ResponseErrorCode.USER_NOT_FOUND),
            @ApiErrorCodeExample(ResponseErrorCode.USER_NOT_ALLOWED),
    })
    @PostMapping("/role")
    public ResponseEntity<UserResponseDto.UserInfo> userInfo(UserRequestDto.UserRole userRole) {
        userService.checkAdmin();
        return ResponseEntity.ok(UserResponseDto.UserInfo.of(userService.updateUserRole(userRole)));
    }
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "유저 로그아웃", description = "로그아웃하기")
    @ApiResponse(
            responseCode = "200",
            description = "정상 응답")
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(ResponseErrorCode.INTERNAL_SERVER_ERROR),
            @ApiErrorCodeExample(ResponseErrorCode.USER_UNAUTHORIZED),
            @ApiErrorCodeExample(ResponseErrorCode.USER_NOT_FOUND),
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> userLogout(HttpServletResponse response) {
        userService.logout(response);
        return ResponseEntity.ok().build();
    }
}