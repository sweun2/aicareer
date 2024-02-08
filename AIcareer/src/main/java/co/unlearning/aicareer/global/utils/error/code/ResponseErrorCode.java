package co.unlearning.aicareer.global.utils.error.code;

import co.unlearning.aicareer.global.utils.error.ExplainError;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Field;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum ResponseErrorCode {
    @ExplainError("서버 내부 에러, 문의 바래요.")
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "COMMON_001", "서버 내부 에러"),
    @ExplainError("입력하는 enum 값에 대해 잘못된 값을 입력한 경우, 각 enum 값 철자를 확인 하세요. ")
    INVALID_ENUM_STRING_INPUT(HttpStatus.BAD_REQUEST.value(), "COMMON_002","잘못된 입력 값입니다."),
    @ExplainError("입력하는 date 값에 대해 입력 패턴이 틀린 경우, 'yyyy-MM-dd HH:mm' 형식 으로 입력해야 합니다.")
    INVALID_DATE_STRING_INPUT(HttpStatus.BAD_REQUEST.value(), "COMMON_003","잘못된 date 입력 값입니다."),
    @ExplainError("입력하는 date 값이 유효하지 않은 경우,마감 일자 입력은 현재 보다 나중이어야 합니다.")
    DATE_BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), "COMMON_004","잘못된 date 입력 값입니다."),

    @ExplainError("DB에 유저 정보가 없는 경우, 회원 가입 필요")
    USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "USER_001", "유저 정보가 없습니다."),
    @ExplainError("요청 end point에 대한 권한이 없는 경우, User role을 ADMIN 으로 변경 필요,DB에서 직접 변경해야 함")
    USER_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED.value(),"USER_002","유저 권한이 부족합니다."),
    @ExplainError("access-token/refresh-token 정보가 잘못된 경우, 재로그인 필요")
    USER_UNAUTHORIZED(HttpStatus.UNAUTHORIZED.value(),"USER_003","로그인에 실패했습니다."),

    @ExplainError("uid가 잘못되었을 때 발생하는 에러, uid 확인 필요")
    UID_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "RECRUITMENT_001", "uid가 잘못 입력되었습니다."),
    @ExplainError("정렬 조건이 잘못 입력된 경우 ")
    SORT_CONDITION_BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), "RECRUITMENT_002", "sort condition 에러."),

    @ExplainError("북마크가 이미 추가되었습니다.")
    BOOKMARK_ALREADY_EXIST(HttpStatus.BAD_REQUEST.value(), "BOOKMARK_001", "북마크가 이미 추가된 상태입니다."),


    @ExplainError("서버에 저장된 파일 위치와 입력한 파일 위치가 일치하지 않습니다.  '/home/app/img/test-image.jpg' 형식으로 입력해야 합니다. ")
    INVALID_IMAGE_URL(HttpStatus.BAD_REQUEST.value(), "IMAGE_001","잘못된 image url 입력 값입니다."),
    @ExplainError("파일을 읽을 수 없습니다. 올바른 이미지 파일인지 확인해 주세요. ")
    NOT_FOUND_IMAGE_FILE(HttpStatus.NOT_FOUND.value(), "IMAGE_002","image 파일이 비어있습니다."),
    @ExplainError("이미지 파일이 형식 에러,이미지 파일 형식을 확인해 주세요. jpg, png 만 가능합니다. ")
    INVALID_IMAGE_CONTENT_TYPE(HttpStatus.BAD_REQUEST.value(), "IMAGE_003","잘못된 image 파일 형식입니다."),
    ;
    private final int status;
    private final String code;
    private final String reason;

    public ErrorReason getErrorReason() {
        return ErrorReason.builder().reason(reason).code(code).status(status).build();
    }
    public String getExplainError() {
        try {
            Field field = this.getClass().getDeclaredField(this.name());
            field.setAccessible(true);  // 필드에 접근할 수 있도록 설정
            ExplainError annotation = field.getAnnotation(ExplainError.class);
            return Objects.nonNull(annotation) ? annotation.value() : this.getReason();
        } catch (NoSuchFieldException | SecurityException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
