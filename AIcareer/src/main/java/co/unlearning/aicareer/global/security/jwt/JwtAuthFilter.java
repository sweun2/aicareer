package co.unlearning.aicareer.global.security.jwt;

import co.unlearning.aicareer.domain.user.User;
import co.unlearning.aicareer.domain.user.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Slf4j
@AllArgsConstructor
public class JwtAuthFilter extends GenericFilterBean {
    private final TokenService tokenService;
    private final UserService userService;
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        String token = null;
        if (request instanceof HttpServletRequest request2) {
            Cookie[] cookies = request2.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    String cookieName = cookie.getName();
                    String cookieValue = cookie.getValue();
                    if(Objects.equals(cookieName, "_aT")) {
                        token = cookieValue;
                    }
                }
            }
        }
        if(token != null) {
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
    private boolean isLogoutRequest(ServletRequest request) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();
        return requestURI.endsWith("/api/user/logout");
    }
    public Authentication getAuthentication(User user){
        return new UsernamePasswordAuthenticationToken(user, "",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
