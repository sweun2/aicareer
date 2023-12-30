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
    @ExplainError("DB에 유저 정보가 없는 경우, 회원 가입 필요")
    USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "RECRUITMENT_001", "유저 정보가 없습니다."),
    @ExplainError("요청 end point에 대한 권한이 없는 경우, User role을 ADMIN 으로 변경 필요,DB에서 직접 변경해야 함")
    USER_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED.value(),"RECRUITMENT_002","유저 권한이 부족합니다."),
    @ExplainError("access-token/refresh-token 정보가 잘못된 경우, 재로그인 필요")
    USER_UNAUTHORIZED(HttpStatus.UNAUTHORIZED.value(),"RECRUITMENT_003","로그인에 실패했습니다."),
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
