package blogengine.services;

import blogengine.mappers.UserDtoMapper;
import blogengine.models.User;
import blogengine.models.dto.requests.LoginRequest;
import blogengine.models.dto.userdto.LoginDto;
import blogengine.models.dto.userdto.LoginInfo;
import blogengine.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.NoSuchElementException;

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

    public LoginInfo login(@RequestBody LoginRequest loginRequest) {

        User user = userDetailsService.loadUserByUsername(loginRequest.getEmail());
        LoginDto loginDto = userDtoMapper.userToLoginDto(user);
        return new LoginInfo(true, loginDto);
    }
}
