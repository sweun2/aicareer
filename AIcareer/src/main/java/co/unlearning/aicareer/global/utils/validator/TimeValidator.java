package co.unlearning.aicareer.global.utils.validator;

import co.unlearning.aicareer.global.utils.error.exception.BusinessException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static co.unlearning.aicareer.global.utils.error.code.ResponseErrorCode.DATE_BAD_REQUEST;
import static co.unlearning.aicareer.global.utils.error.code.ResponseErrorCode.INVALID_DATE_STRING_INPUT;

public class TimeValidator {
    public static LocalDateTime AllowedLocalDateTimeValidator(String time) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
            return LocalDateTime.parse(time +":00.000",formatter);
        } catch (Exception e) {
            throw new BusinessException(INVALID_DATE_STRING_INPUT);
        }
    }
    public static boolean RemainingTimeValidator(LocalDateTime localDateTime) {
        if(LocalDateTime.now().isBefore(localDateTime)) {
            return true;
        }
        else {
            throw new BusinessException(DATE_BAD_REQUEST);
        }
    }
}
