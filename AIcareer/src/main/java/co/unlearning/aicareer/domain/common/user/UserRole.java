package co.unlearning.aicareer.domain.common.user;

import lombok.Getter;

@Getter
public enum UserRole {
    USER("USER")
    ,ADMIN("ADMIN")
    ,GUEST("GUEST")
    ,PERMANENT_BLOCK("PERMANENT_BLOCK");

    private final String userRole;
    UserRole(String userRole) {
        this.userRole = userRole;
    }
}
