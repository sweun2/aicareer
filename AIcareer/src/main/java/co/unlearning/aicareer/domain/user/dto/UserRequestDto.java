package co.unlearning.aicareer.domain.user.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import java.util.List;

public class UserRequestDto {
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginForm {
        @NotEmpty
        private String email;
        @NotEmpty
        private String password;
    }
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserRole {
        @NotEmpty
        private String UserRole;
    }

}
