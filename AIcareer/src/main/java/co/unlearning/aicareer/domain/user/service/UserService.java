package co.unlearning.aicareer.domain.user.service;


import co.unlearning.aicareer.domain.user.User;
import co.unlearning.aicareer.domain.user.UserRole;
import co.unlearning.aicareer.domain.user.dto.UserRequestDto;
import co.unlearning.aicareer.domain.user.repository.UserRepository;
import co.unlearning.aicareer.global.utils.error.code.UserErrorCode;
import co.unlearning.aicareer.global.utils.error.exception.BusinessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;



@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    public User getUserByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(
                ()->new BusinessException(UserErrorCode.USER_NOT_FOUND)
        );
    }
    public User getLoginUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal().equals("anonymousUser")){
            throw new BusinessException(UserErrorCode.USER_UNAUTHORIZED);
        }
        User user = (User) authentication.getPrincipal();
        return userRepository.findById(user.getId()).orElseThrow(
                ()-> new BusinessException(UserErrorCode.USER_NOT_FOUND));
    }

    public Boolean verifyLoginUser(User user) {
        User user1 = userRepository.findByEmail(user.getEmail()).orElseThrow(
                () -> new BusinessException(UserErrorCode.USER_NOT_FOUND)
        );

        if (!user1.getEmail().equals(user.getEmail())) {
            throw new BusinessException(UserErrorCode.USER_NOT_FOUND);
        } else if (!user1.getPassword().equals(user.getPassword())) {
            throw new BusinessException(UserErrorCode.USER_UNAUTHORIZED);
        } else return true;
    }
    public User updateUserRole(UserRequestDto.UserRole userRole) {
        User user = getLoginUser();
        if(user.getUserRole() != UserRole.ADMIN) {
            throw new BusinessException(UserErrorCode.USER_NOT_ALLOWED);
        }
        try {
            user.setUserRole(UserRole.valueOf(userRole.getUserRole()));
            userRepository.save(user);
        }catch (Exception e) {
            log.info(e.getMessage());
        }
        return user;
    }
}
