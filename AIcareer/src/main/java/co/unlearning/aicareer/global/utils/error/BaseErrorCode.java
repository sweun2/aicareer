package co.unlearning.aicareer.global.utils.error;

import lombok.Getter;

public interface BaseErrorCode {
    public ErrorReason getErrorReason();

    String getExplainError() throws NoSuchFieldException;

}
