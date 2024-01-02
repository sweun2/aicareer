package co.unlearning.aicareer.global.utils.error.code;

import co.unlearning.aicareer.global.utils.error.ExplainError;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Field;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum CommonErrorCode implements BaseErrorCode {
    @ExplainError("서버 내부 에러, 문의 바래요.")
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "COMMON_001", "서버 내부 에러"),
    @ExplainError("입력하는 enum 값에 대해 잘못된 값을 입력한 경우, 각 enum 값 철자를 확인 하세요. ")
    INVALID_ENUM_STRING_INPUT(HttpStatus.BAD_REQUEST.value(), "COMMON_002","잘못된 입력 값입니다."),
    @ExplainError("입력하는 date 값에 대해 입력 패턴이 틀린 경우, 'yyyy-MM-dd HH:mm' 형식 으로 입력해야 합니다.")
    INVALID_DATE_STRING_INPUT(HttpStatus.BAD_REQUEST.value(), "COMMON_003","잘못된 date 입력 값입니다."),
    @ExplainError("입력하는 date 값이 유효하지 않은 경우,마감 일자 입력은 현재 보다 나중이어야 합니다.")
    DATE_BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), "COMMON_004","잘못된 date 입력 값입니다."),
    ;
    private final int status;
    private final String code;
    private final String reason;

    @Override
    public ErrorReason getErrorReason() {
        return ErrorReason.builder().reason(reason).code(code).status(status).build();
    }
    @Override
    public String getExplainError() throws NoSuchFieldException {
        Field field = this.getClass().getField(this.name());
        ExplainError annotation = field.getAnnotation(ExplainError.class);
        return Objects.nonNull(annotation) ? annotation.value() : this.getReason();
    }
}
