package co.unlearning.aicareer.global.utils.converter;

import co.unlearning.aicareer.global.utils.validator.TimeValidator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeStringConverter {
    public static String LocalDateTimeToString(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
    public static LocalDateTime StringToLocalDateTime(String stringTime) {
        return TimeValidator.AllowedLocalDateTimeValidator(stringTime);
    }
}
