package blogengine.services;

import blogengine.mappers.UserDtoMapper;
import blogengine.models.User;
import blogengine.models.dto.requests.LoginRequest;
import blogengine.models.dto.userdto.UserLoginDto;
import blogengine.models.dto.userdto.UserLoginInfo;
import blogengine.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.nio.file.attribute.UserPrincipalNotFoundException;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;
    private UserDtoMapper userDtoMapper;


    public User findById(Integer id){
        return userRepository.findById(id).orElse(null);
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email).orElse(null);
    }

    public UserLoginInfo login(@RequestBody LoginRequest loginRequest) throws UserPrincipalNotFoundException {

        User user = userRepository.findByEmail(loginRequest.getEmail()).orElse(null);
        if (user == null){
            throw new UserPrincipalNotFoundException("Не найден пользователь с таким адресом почты: " + loginRequest.getEmail());
        }
        UserLoginDto loginDto = userDtoMapper.userToLoginDto(user);
        return new UserLoginInfo(true, loginDto);
    }

    //TODO Заглушка, сделать нормально
    public UserLoginInfo check(){
        User user = userRepository.findById(3).get();
        UserLoginDto userDTO = userDtoMapper.userToLoginDto(user);
        return new UserLoginInfo(true, userDTO);
    }
}
