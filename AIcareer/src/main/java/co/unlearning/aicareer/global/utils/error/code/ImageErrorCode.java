package co.unlearning.aicareer.global.utils.error.code;

import co.unlearning.aicareer.global.utils.error.ExplainError;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Field;
import java.util.Objects;

@RequiredArgsConstructor
@Getter
public enum ImageErrorCode implements BaseErrorCode {
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
