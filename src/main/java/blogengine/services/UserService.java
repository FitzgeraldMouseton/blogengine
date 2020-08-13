package blogengine.services;

import blogengine.models.User;
import blogengine.repositories.UserRepository;
import blogengine.util.SessionStorage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SessionStorage sessionStorage;

    public User findById(final Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    public User findByEmail(final String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User findByCode(final String code) {
        return userRepository.findByCode(code).orElse(null);
    }

    public void save(final User user) {
        userRepository.save(user);
    }

    public User getCurrentUser() {
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        if (!sessionStorage.getSessions().containsKey(sessionId)) {
            return null;
        }
        Integer userId = sessionStorage.getSessions().get(sessionId);
        return userRepository.findById(userId).orElse(null);
    }
}
