package co.unlearning.aicareer.domain.common.user;

import lombok.Getter;

@Getter
public enum UserRole {
    GUEST("USER")
    ,ADMIN("ADMIN")
    ,USER("USER")
    ,BLOCK("BLOCK");

    private final String userRole;
    UserRole(String userRole) {
        this.userRole = userRole;
    }
}
