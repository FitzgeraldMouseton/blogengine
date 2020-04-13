package blogengine.services;

import blogengine.mappers.UserDtoMapper;
import blogengine.models.User;
import blogengine.models.dto.requests.LoginRequest;
import blogengine.models.dto.userdto.UserLoginDto;
import blogengine.models.dto.userdto.UserLoginInfo;
import blogengine.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpSession;

@Slf4j
@Service
public class UserService {

    private UserRepository userRepository;
    private UserDtoMapper userDtoMapper;
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    public UserService(UserRepository userRepository, UserDtoMapper userDtoMapper, UserDetailsServiceImpl userDetailsService) {
        this.userRepository = userRepository;
        this.userDtoMapper = userDtoMapper;
        this.userDetailsService = userDetailsService;
    }

    public User findById(Integer id){
        return userRepository.findById(id).orElse(null);
    }

    public UserLoginInfo login(@RequestBody LoginRequest loginRequest) {

        User user = userDetailsService.loadUserByUsername(loginRequest.getEmail());
        UserLoginDto loginDto = userDtoMapper.userToLoginDto(user);
        return new UserLoginInfo(true, loginDto);
    }

    //TODO Заглушка, сделать нормально
    public UserLoginInfo check(){
        User user = userRepository.findById(10).get();
        UserLoginDto userDTO = userDtoMapper.userToLoginDto(user);
        return new UserLoginInfo(true, userDTO);
    }
}
