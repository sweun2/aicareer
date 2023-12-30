package co.unlearning.aicareer.domain.user.dto;

import co.unlearning.aicareer.domain.user.User;
import co.unlearning.aicareer.domain.user.UserRole;
import jakarta.persistence.Column;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Builder
@Getter @Setter
@AllArgsConstructor
public class UserResponseDto {
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Simple {
        private Integer userId;
        private String name;
        private String nickname;
        public static Simple of(User user) {
            return Simple.builder()
                    .userId(user.getId())
                    .name(user.getName())
                    .nickname(user.getNickname())
                    .build();
        }

        public static List<Simple> of(List<User> users) {
            return users.stream().map(Simple::of).collect(Collectors.toList());
        }
    }
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Info {
        private String nickname;
        private String name;
        private String email;
        private String joinDate;
        private UserRole userRole;
        public static Info of(User user) {
            return Info.builder()
                    .nickname(user.getNickname())
                    .name(user.getName())
                    .email(user.getEmail())
                    .joinDate(String.valueOf(user.getJoinDate()))
                    .build();
        }

        public static List<Info> of(List<User> users) {
            return users.stream().map(Info::of).collect(Collectors.toList());
        }
    }
}

