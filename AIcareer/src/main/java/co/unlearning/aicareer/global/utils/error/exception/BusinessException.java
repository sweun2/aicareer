package co.unlearning.aicareer.global.utils.error.exception;

import co.unlearning.aicareer.global.utils.error.code.BaseErrorCode;
import co.unlearning.aicareer.global.utils.error.code.ResponseErrorCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final ResponseErrorCode responseErrorCode;

    public BusinessException(ResponseErrorCode responseErrorCode) {
        super(responseErrorCode.getErrorReason().getReason());
        this.responseErrorCode = responseErrorCode;
    }
}
