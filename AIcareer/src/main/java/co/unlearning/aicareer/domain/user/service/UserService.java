package co.unlearning.aicareer.domain.user.service;


import co.unlearning.aicareer.domain.user.User;
import co.unlearning.aicareer.domain.user.UserRole;
import co.unlearning.aicareer.domain.user.dto.UserRequestDto;
import co.unlearning.aicareer.domain.user.repository.UserRepository;
import co.unlearning.aicareer.global.security.jwt.Token;
import co.unlearning.aicareer.global.security.jwt.TokenService;
import co.unlearning.aicareer.global.utils.error.code.ResponseErrorCode;
import co.unlearning.aicareer.global.utils.error.exception.BusinessException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final TokenService tokenService;
    public User getUserByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(
                ()->new BusinessException(ResponseErrorCode.USER_NOT_FOUND)
        );
    }
    public User getLoginUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal().equals("anonymousUser")){
            log.info("annnoy");
            throw new BusinessException(ResponseErrorCode.USER_UNAUTHORIZED);
        }
        User user = (User) authentication.getPrincipal();
        log.info("getLoginUser");

        return userRepository.findById(user.getId()).orElseThrow(
                ()-> new BusinessException(ResponseErrorCode.USER_NOT_FOUND));
    }

    public Boolean verifyLoginUser(User user) {
        User user1 = userRepository.findByEmail(user.getEmail()).orElseThrow(
                () -> new BusinessException(ResponseErrorCode.USER_NOT_FOUND)
        );

        if (!user1.getEmail().equals(user.getEmail())) {
            throw new BusinessException(ResponseErrorCode.USER_NOT_FOUND);
        } else if (!user1.getPassword().equals(user.getPassword())) {
            throw new BusinessException(ResponseErrorCode.USER_UNAUTHORIZED);
        } else return true;
    }
    public User updateUserRole(UserRequestDto.UserRole userRole) {
        User user = getLoginUser();
        if(user.getUserRole() != UserRole.ADMIN) {
            throw new BusinessException(ResponseErrorCode.USER_NOT_ALLOWED);
        }
        try {
            user.setUserRole(UserRole.valueOf(userRole.getUserRole()));
            userRepository.save(user);
        }catch (Exception e) {
            log.info(e.getMessage());
        }
        return user;
    }
    public void logout(HttpServletResponse response) {
        User user = getLoginUser();

        Cookie accessToken = new Cookie("accessToken", "");
        accessToken.setMaxAge(0);
        accessToken.setHttpOnly(true);
        accessToken.setPath("/");
        response.addCookie(accessToken);

        //refresh token -> 쿠키로 전달, access token -> 쿼리 스트링으로 전달
        Cookie refreshToken = new Cookie("refreshToken", "");
        refreshToken.setMaxAge(0);
        refreshToken.setHttpOnly(true);
        refreshToken.setPath("/");
        response.addCookie(refreshToken);

    }
}
