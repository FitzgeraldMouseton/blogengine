package blogengine.services;

import blogengine.mappers.UserDtoMapper;
import blogengine.models.User;
import blogengine.models.dto.SuccessDto;
import blogengine.models.dto.requests.LoginRequest;
import blogengine.models.dto.userdto.UserLoginDto;
import blogengine.models.dto.userdto.UserLoginInfo;
import blogengine.repositories.UserRepository;
import blogengine.util.mail.EmailServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;
    private UserDtoMapper userDtoMapper;
    private EmailServiceImpl emailService;


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

        if (user.getPassword().equals(loginRequest.getPassword())){

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

    public SuccessDto sendRestorePasswordMessage(String email){
        String code = UUID.randomUUID().toString();
        log.info(email);
        User user = findByEmail(email);
        user.setCode(code);
        userRepository.save(user);
        String message = "Для восстановления пароля пейдите по ссылке http://localhost:8080/login/change-password/" + code;
        emailService.send(email, "Васстановление пароля", message);
        return new SuccessDto();
    }
}
