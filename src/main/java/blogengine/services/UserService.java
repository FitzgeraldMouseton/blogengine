package blogengine.services;

import blogengine.mappers.UserDtoMapper;
import blogengine.models.User;
import blogengine.models.dto.requests.LoginRequest;
import blogengine.models.dto.userdto.LoginDto;
import blogengine.models.dto.userdto.LoginInfo;
import blogengine.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.NoSuchElementException;

@Slf4j
@Service
public class UserService {

    private UserRepository userRepository;
    private UserDtoMapper userDtoMapper;

    @Autowired
    public UserService(UserRepository userRepository, UserDtoMapper userDtoMapper) {
        this.userRepository = userRepository;
        this.userDtoMapper = userDtoMapper;
    }

//    public LoginInfo login(@RequestBody LoginRequest loginRequest) {
//
//        log.info(loginRequest.getEmail());
//        User user = userRepository.findByEmail(loginRequest.getEmail()).orElse(null);
//        if (user == null) {
//            throw new NoSuchElementException("Пользователя с таким email не существует");
//        }
//        LoginDto loginDto = userDtoMapper.userToLoginDto(user);
//        return new LoginInfo(true, loginDto);
//    }
}
