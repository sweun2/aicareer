package co.unlearning.aicareer.global.security.jwt;

import co.unlearning.aicareer.domain.user.User;
import co.unlearning.aicareer.domain.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final UserRepository userRepository;
    @Value("${jwt.secret_key}")
    private String SECRET_KEY;
    private Key key;
    private final long ACCESS_EXPIRE = 1000 * 60 * 60;             //60분
    private final long REFRESH_EXPIRE = 1000 * 60 * 60 * 24 * 7;   //7일

    @PostConstruct
    protected void init(){
        key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    public Claims generateClaims(String uid, String role){
        Claims claims = Jwts.claims().setSubject(uid);
        claims.put("role", role);
        return claims;
    }

    public Token generateToken(String uid, String role){
        Claims claims = generateClaims(uid, role);
        Date issueDate = new Date(); //토큰 발행 시각

        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(issueDate)
                .setExpiration(new Date(issueDate.getTime() + ACCESS_EXPIRE))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(issueDate)
                .setExpiration(new Date(issueDate.getTime() + REFRESH_EXPIRE))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // 디버깅을 위한 출력
        System.out.println("AccessToken: " + accessToken);
        System.out.println("RefreshToken: " + refreshToken);

        return new Token(accessToken, refreshToken);
    }

    public boolean verifyToken(String token){
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build().parseClaimsJws(token);
            return claims.getBody()
                    .getExpiration()
                    .after(new Date());
        } catch (Exception e){
            return false;
        }
    }

    @Transactional
    public Token refresh(HttpServletRequest request){
        String accessToken = request.getHeader("access-token");
        String refreshToken = getRefreshTokenFromCookie(request)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "refresh token이 없습니다."));

        //access token에서 user 가져오기
        String email = getUid(accessToken);
        if(!verifyToken(refreshToken)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "refresh token이 유효하지 않습니다.");
        }

        //토큰 재발급
        return generateToken(email, "USER");
    }

    public Optional<String> getRefreshTokenFromCookie(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for(Cookie cookie : cookies){
                if(cookie.getName().equals("refresh-token")){
                    return Optional.of(cookie.getValue());
                }
            }
        }
        return Optional.empty();
    }

    public String getUid(String token){
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }
}
