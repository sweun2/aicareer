package co.unlearning.aicareer.domain.common.user.controller;

import co.unlearning.aicareer.domain.common.user.User;
import co.unlearning.aicareer.domain.common.user.UserTerms;
import co.unlearning.aicareer.domain.common.user.dto.UserRequestDto;
import co.unlearning.aicareer.domain.common.user.dto.UserResponseDto;
import co.unlearning.aicareer.domain.common.user.repository.UserRepository;
import co.unlearning.aicareer.domain.common.user.service.UserService;
import co.unlearning.aicareer.global.security.jwt.TokenService;
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
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Tag(name = "user", description = "유저 api")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final TokenService tokenService;
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
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "유저 리스트 가져오기", description = "현재 로그인된 유저의 기본 정보를 가져옵니다.")
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
    @GetMapping("/all")
    public ResponseEntity<List<UserResponseDto.UserSimple>> findAllUser() {
        return ResponseEntity.ok(UserResponseDto.UserSimple.of(userService.getAllUser()));
    }
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "유저 약관/개인정보 제공 동의", description = "약관 및 개인정보 제공 동의")
    @ApiResponse(
            responseCode = "200",
            description = "정상 응답",
            useReturnTypeSchema = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResponseDto.UserInfo.class)
            ))
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(ResponseErrorCode.INTERNAL_SERVER_ERROR),
            @ApiErrorCodeExample(ResponseErrorCode.INVALID_DATE_STRING_INPUT),
            @ApiErrorCodeExample(ResponseErrorCode.USER_UNAUTHORIZED),
            @ApiErrorCodeExample(ResponseErrorCode.USER_NOT_FOUND),
    })
    @PatchMapping("/user-terms")
    public ResponseEntity<UserResponseDto.UserInfo> updateUserTerms(@RequestBody UserRequestDto.UserTermsInfo userTermsInfo) {
        return ResponseEntity.ok(UserResponseDto.UserInfo.of(userService.updateUserTerms(userTermsInfo)));
    }
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "유저 관심사 생성", description = "유저 관심사 수정")
    @ApiResponse(
            responseCode = "200",
            description = "정상 응답",
            useReturnTypeSchema = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResponseDto.UserInfo.class)
            ))
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(ResponseErrorCode.INTERNAL_SERVER_ERROR),
            @ApiErrorCodeExample(ResponseErrorCode.INVALID_DATE_STRING_INPUT),
            @ApiErrorCodeExample(ResponseErrorCode.USER_UNAUTHORIZED),
            @ApiErrorCodeExample(ResponseErrorCode.USER_NOT_FOUND),
    })
    @PostMapping("/user-interest")
    public ResponseEntity<UserResponseDto.UserInfo> postUserInterest(@RequestBody UserRequestDto.UserInterestInfo userInterestInfo) {
        return ResponseEntity.ok(UserResponseDto.UserInfo.of(userService.setUserInterest(userInterestInfo)));
    }
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "유저 관심사 가져오기", description = "유저 관심사 가져오기")
    @ApiResponse(
            responseCode = "200",
            description = "정상 응답",
            useReturnTypeSchema = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResponseDto.UserInterestInfo.class)
            ))
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(ResponseErrorCode.INTERNAL_SERVER_ERROR),
            @ApiErrorCodeExample(ResponseErrorCode.INVALID_DATE_STRING_INPUT),
            @ApiErrorCodeExample(ResponseErrorCode.USER_UNAUTHORIZED),
            @ApiErrorCodeExample(ResponseErrorCode.USER_NOT_FOUND),
    })
    @GetMapping("/user-interest")
    public ResponseEntity<UserResponseDto.UserInterestInfo> getUserInterest() {
        return ResponseEntity.ok(UserResponseDto.UserInterestInfo.of(userService.getUserInterest()));
    }
    @GetMapping("/test")
    public ResponseEntity<String> getToken() {
        return ResponseEntity.ok(tokenService.generateToken("sweun3@gmail.com","USER").getAccessToken());
    }

}