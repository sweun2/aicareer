package co.unlearning.aicareer.domain.user;

import lombok.Getter;

@Getter
public enum UserRole {
    USER("USER")
    ,ADMIN("ADMIN")
    ,BLOCK("BLOCK");

    private final String userRole;
    UserRole(String userRole) {
        this.userRole = userRole;
    }
}
