package co.unlearning.aicareer.global.utils.error.handler;

import co.unlearning.aicareer.global.utils.error.code.BaseErrorCode;
import co.unlearning.aicareer.global.utils.error.code.CommonErrorCode;
import co.unlearning.aicareer.global.utils.error.dto.ErrorResponse;
import co.unlearning.aicareer.global.utils.error.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        BaseErrorCode baseErrorCode = e.getBaseErrorCode();
        ErrorResponse response = new ErrorResponse(baseErrorCode.getErrorReason());
        return new ResponseEntity<>(response, HttpStatus.valueOf(baseErrorCode.getErrorReason().getStatus()));
    }
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        BaseErrorCode baseErrorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;
        ErrorResponse response = new ErrorResponse(baseErrorCode.getErrorReason());
        return new ResponseEntity<>(response, HttpStatus.valueOf(baseErrorCode.getErrorReason().getStatus()));
    }
}
