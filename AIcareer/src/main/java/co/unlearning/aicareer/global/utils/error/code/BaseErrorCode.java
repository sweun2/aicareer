package co.unlearning.aicareer.global.utils.error.code;

import co.unlearning.aicareer.global.utils.error.ExplainError;
import lombok.Getter;
import org.springframework.http.HttpStatus;

public interface BaseErrorCode {
    public ErrorReason getErrorReason();

    String getExplainError();
}
