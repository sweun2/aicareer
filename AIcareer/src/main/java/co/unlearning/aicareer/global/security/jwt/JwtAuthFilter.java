package co.unlearning.aicareer.global.security.jwt;

import co.unlearning.aicareer.domain.user.User;
import co.unlearning.aicareer.domain.user.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends GenericFilterBean {
    private final TokenService tokenService;
    private final UserService userService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        Cookie[] list = ((HttpServletRequest) request).getCookies();
        if(list != null) {
            String token = "token";
            for (Cookie cookie : list) {
                if (cookie.getName().equals("accessToken")) {
                    token = cookie.getValue();
                }
            }

            if (tokenService.verifyToken(token)) {
                String email = tokenService.getUid(token);
                User user = userService.getUserByEmail(email);
                log.info(user.getEmail());


                Authentication auth = getAuthentication(user);
                SecurityContextHolder.getContext().setAuthentication(auth);

            }
        }

        chain.doFilter(request, response);
    }

    public Authentication getAuthentication(User user){
        return new UsernamePasswordAuthenticationToken(user, "",
                Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
