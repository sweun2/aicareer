package co.unlearning.aicareer.domain.common.user;

import lombok.Getter;

@Getter
public enum UserRole {
    USER("USER")
    ,ADMIN("ADMIN")
    ,GUEST("GUEST")
    ,BLOCK("BLOCK");

    private final String userRole;
    UserRole(String userRole) {
        this.userRole = userRole;
    }
}
