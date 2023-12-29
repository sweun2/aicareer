package co.unlearning.aicareer.global.security.jwt;

import lombok.*;

@ToString
@NoArgsConstructor
@Getter
@Builder
@AllArgsConstructor
public class Token {
    private String accessToken;
    private String refreshToken;
}
