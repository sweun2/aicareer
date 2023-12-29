package co.unlearning.aicareer.domain.user.service;


import co.unlearning.aicareer.domain.user.User;
import co.unlearning.aicareer.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;



@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    public User getUserByPhone(String phone){
        return userRepository.findByPhone(phone).orElseThrow(
                ()->new ResponseStatusException(HttpStatus.NOT_FOUND,"phone 없음")
        );
    }
    public User getLoginUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal().equals("anonymousUser")){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"로그인 재시도 필요");
        }
        User user = (User) authentication.getPrincipal();
        return userRepository.findById(user.getId()).orElseThrow(
                ()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"유저 없음"));
    }

    public Boolean verifyLoginUser(User user) {

        User user1 = userRepository.findByPhone(user.getPhone()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다.")
        );

        if (!user1.getPhone().equals(user.getPhone())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "잘못된 이메일입니다.");
        } else if (!user1.getPassword().equals(user.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "잘못된 비밀번호 입니다.");
        } else return true;
    }
}
