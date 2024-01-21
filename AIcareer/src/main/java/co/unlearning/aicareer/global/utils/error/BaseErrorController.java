package co.unlearning.aicareer.global.utils.error;

import co.unlearning.aicareer.global.utils.error.code.ResponseErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@Tag(name = "error", description = "에러 list")
@RequiredArgsConstructor
@RequestMapping("/api/err")
public class BaseErrorController {
    @Operation(summary = "에러 메시지 종합", description = "모든 발생 가능한 에러 메세지 표시")
    @ApiResponse(
            responseCode = "200",
            description = "정상 응답")
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(ResponseErrorCode.INTERNAL_SERVER_ERROR),
            @ApiErrorCodeExample(ResponseErrorCode.INVALID_ENUM_STRING_INPUT),
            @ApiErrorCodeExample(ResponseErrorCode.INVALID_DATE_STRING_INPUT),
            @ApiErrorCodeExample(ResponseErrorCode.USER_NOT_FOUND),
            @ApiErrorCodeExample(ResponseErrorCode.USER_NOT_ALLOWED),
            @ApiErrorCodeExample(ResponseErrorCode.USER_UNAUTHORIZED),
            @ApiErrorCodeExample(ResponseErrorCode.UID_NOT_FOUND),
            @ApiErrorCodeExample(ResponseErrorCode.SORT_CONDITION_BAD_REQUEST),
            @ApiErrorCodeExample(ResponseErrorCode.INVALID_IMAGE_URL),
            @ApiErrorCodeExample(ResponseErrorCode.NOT_FOUND_IMAGE_FILE),
            @ApiErrorCodeExample(ResponseErrorCode.INVALID_IMAGE_CONTENT_TYPE),
    })
    @PostMapping("/")
    public void errList() {}
}
