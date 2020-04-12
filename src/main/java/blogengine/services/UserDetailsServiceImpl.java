package blogengine.services;

import blogengine.models.User;
import blogengine.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("trig");
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty())
            throw new UsernameNotFoundException(String.format("Пользователь с адресом %s не найден", email));
        return userOptional.get();
    }
}
