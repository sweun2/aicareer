package co.unlearning.aicareer.global.utils.error.code;

import co.unlearning.aicareer.global.utils.error.ExplainError;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Field;
import java.util.Objects;
@Getter
@AllArgsConstructor
public enum RecruitmentErrorCode implements BaseErrorCode{
    @ExplainError("recruitment uid가 잘못되었을 때 발생하는 에러, uid 확인 필요")
    RECRUITMENT_UID_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "RECRUITMENT_001", "uid가 잘못 입력되었습니다."),
    @ExplainError("정렬 조건이 잘못 입력된 경우 ")
    SORT_CONDITION_BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), "RECRUITMENT_002", "sort condition 에러."),
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
