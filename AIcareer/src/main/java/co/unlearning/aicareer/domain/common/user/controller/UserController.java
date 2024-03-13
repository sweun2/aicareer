package co.unlearning.aicareer.domain.common.user.controller;

import co.unlearning.aicareer.domain.common.user.dto.UserRequestDto;
import co.unlearning.aicareer.domain.common.user.dto.UserResponseDto;
import co.unlearning.aicareer.domain.common.user.service.UserService;
import co.unlearning.aicareer.global.utils.error.ApiErrorCodeExample;
import co.unlearning.aicareer.global.utils.error.ApiErrorCodeExamples;
import co.unlearning.aicareer.global.utils.error.code.ResponseErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.gson.JsonObject;
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

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
    @PutMapping("/user-terms")
    public ResponseEntity<UserResponseDto.UserInfo> updateUserTerms(UserRequestDto.UserTermsInfo userTermsInfo) {
        return ResponseEntity.ok(UserResponseDto.UserInfo.of(userService.updateUserTerms(userTermsInfo)));
    }
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "유저 관심사 수정", description = "유저 관심사 수정")
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
    @PutMapping("/user-interest")
    public ResponseEntity<UserResponseDto.UserInterestInfo> updateUserInterest(UserRequestDto.UserInterestInfo userInterestInfo) {
        return ResponseEntity.ok(UserResponseDto.UserInterestInfo.of(userService.updateUserInterest(userInterestInfo)));
    }
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "유저 관심사 가져오기", description = "유저 관심사 가져오기")
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
    @GetMapping("/user-interest")
    public ResponseEntity<UserResponseDto.UserInterestInfo> getUserInterest() {
        return ResponseEntity.ok(UserResponseDto.UserInterestInfo.of(userService.getUserInterest()));
    }
    @ResponseBody
    @GetMapping("/kakao")
    public ResponseEntity<String> kakaoCallback(@RequestParam String code) throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-type","application/x-www-form-urlencoded;chartset=utf-8");
        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        String REST_API_KEY = "567a3bba3ec24bb1cabaadaab32a2325";
        String REDIRECT_URI = "http://localhost:8080/api/user/kakao";
        params.add("grant_type","authorization_code");
        params.add("client_id",REST_API_KEY);
        params.add("redirect_uri",REDIRECT_URI);
        params.add("code",code);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<MultiValueMap<String,String>> kakaoToken = new HttpEntity<>(params,httpHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoToken,
                String.class
        );
        JSONObject object = new JSONObject(response.getBody());

        String accessToken = object.get("access_token").toString();
        log.info("at:{}",accessToken);
        sendKakaoMessage(accessToken);

        return ResponseEntity.ok(response.getBody());
    }
    public void sendKakaoMessage(String accessToken) {
        // RestTemplate에 FormHttpMessageConverter와 StringHttpMessageConverter 추가
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter(StandardCharsets.UTF_8));

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", "Bearer " + accessToken);

        String shortenUrl = shortenUrlFromNaver();
        // JSON 문자열 직접 구성
        String templateObjectJson = "{\"object_type\":\"text\",\"text\":\""+ shortenUrl +"\",\"link\":{\"web_url\":\"https://developers.kakao.com\",\"mobile_web_url\":\"https://developers.kakao.com\"},\"button_title\":\"바로 확인\"}";
        // MultiValueMap을 사용하여 form 데이터 구성
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("template_object", templateObjectJson);

        // HttpEntity 구성
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(map, headers);

        // POST 요청 실행
        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://kapi.kakao.com/v2/api/talk/memo/default/send", requestEntity, String.class);

        // 응답 출력
        System.out.println(response.getBody());
    }
    public String shortenUrlFromNaver() {
        RestTemplate restTemplate = new RestTemplate();

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        String clientId = "aY6ehque07j5CjYTBIcX";
        String clientSecret = "73CHBBQ3sD";
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("X-Naver-Client-Id",clientId);
        headers.add("X-Naver-Client-Secret", clientSecret);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("url", "https://aicareer.co.kr");

        // UriComponentsBuilder를 사용하여 URL 인코딩 처리
        URI uri = UriComponentsBuilder.fromHttpUrl("https://openapi.naver.com/v1/util/shorturl")
                .queryParams(map)
                .build()
                .encode()
                .toUri();

        // HttpEntity 구성
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(map, headers);

        // POST 요청 실행
        ResponseEntity<String> response = restTemplate.exchange(
                uri, HttpMethod.POST, requestEntity, String.class);

        JSONObject jsonObject = new JSONObject(response.getBody());
        JSONObject result = (JSONObject) jsonObject.get("result");
        log.info(result.get("url").toString());
        return  result.get("url").toString();
    }
}