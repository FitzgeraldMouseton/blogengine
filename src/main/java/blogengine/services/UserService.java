package blogengine.services;

import blogengine.models.User;
import blogengine.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;

    public User findById(Integer id){
        return userRepository.findById(id).orElse(null);
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email).orElse(null);
    }

    public void save(User user){
        userRepository.save(user);
    }
}
