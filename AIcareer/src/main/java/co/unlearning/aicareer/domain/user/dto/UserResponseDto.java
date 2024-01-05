package co.unlearning.aicareer.domain.user.dto;

import co.unlearning.aicareer.domain.user.User;
import co.unlearning.aicareer.domain.user.UserRole;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

public class UserResponseDto {
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSimple {
        private Integer userId;
        private String name;
        private String nickname;
        public static UserSimple of(User user) {
            return UserSimple.builder()
                    .userId(user.getId())
                    .name(user.getName())
                    .nickname(user.getNickname())
                    .build();
        }

        public static List<UserSimple> of(List<User> users) {
            return users.stream().map(UserSimple::of).collect(Collectors.toList());
        }
    }
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private String nickname;
        private String name;
        private String email;
        private String joinDate;
        private UserRole userRole;
        public static UserInfo of(User user) {
            return UserInfo.builder()
                    .nickname(user.getNickname())
                    .name(user.getName())
                    .email(user.getEmail())
                    .joinDate(String.valueOf(user.getJoinDate()))
                    .build();
        }

        public static List<UserInfo> of(List<User> users) {
            return users.stream().map(UserInfo::of).collect(Collectors.toList());
        }
    }
}

