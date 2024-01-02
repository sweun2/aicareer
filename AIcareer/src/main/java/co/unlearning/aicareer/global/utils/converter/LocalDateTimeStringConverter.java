package co.unlearning.aicareer.global.utils.converter;

import co.unlearning.aicareer.global.utils.error.exception.BusinessException;
import co.unlearning.aicareer.global.utils.validator.TimeValidator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static co.unlearning.aicareer.global.utils.error.code.CommonErrorCode.INVALID_DATE_STRING_INPUT;

public class LocalDateTimeStringConverter {
    public static String LocalDateTimeToString(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
    public static LocalDateTime StringToLocalDateTime(String stringTime) {
        return TimeValidator.AllowedLocalDateTimeValidator(stringTime);
    }
}
