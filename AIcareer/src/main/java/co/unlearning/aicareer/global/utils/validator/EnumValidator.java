package co.unlearning.aicareer.global.utils.validator;

import co.unlearning.aicareer.global.utils.error.exception.BusinessException;

import static co.unlearning.aicareer.global.utils.error.code.CommonErrorCode.INVALID_ENUM_STRING_INPUT;

public class EnumValidator<T extends Enum<T>> {

    public T validateEnumString(String enumString, Class<T> enumClass) {
        try {
            return Enum.valueOf(enumClass, enumString);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new BusinessException(INVALID_ENUM_STRING_INPUT);
        }
    }
}