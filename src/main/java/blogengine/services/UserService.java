package blogengine.services;

import blogengine.models.User;
import blogengine.repositories.UserRepository;
import blogengine.util.SessionStorage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private SessionStorage sessionStorage;

    User findById(Integer id){
        return userRepository.findById(id).orElse(null);
    }

    User findByEmail(String email){
        return userRepository.findByEmail(email).orElse(null);
    }

    User findByCode(String code){
        return userRepository.findByCode(code).orElse(null);
    }

    User getModerator(){
        return userRepository.getModerator().orElse(null);
    }

    public void save(User user){
        userRepository.save(user);
    }

    User getCurrentUser(){
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        if (sessionStorage.getSessions().size() == 0){
            return null;
        }
        Integer userId = sessionStorage.getSessions().get(sessionId);
        return userRepository.findById(userId).orElse(null);
    }
}
