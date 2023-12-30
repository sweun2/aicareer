package co.unlearning.aicareer.global.utils.error.exception;

import co.unlearning.aicareer.global.utils.error.code.BaseErrorCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final BaseErrorCode baseErrorCode;

    public BusinessException(BaseErrorCode baseErrorCode) {
        super(baseErrorCode.getErrorReason().getReason());
        this.baseErrorCode = baseErrorCode;
    }
}
