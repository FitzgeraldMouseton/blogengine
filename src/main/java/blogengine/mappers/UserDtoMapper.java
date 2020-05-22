package blogengine.mappers;

import blogengine.models.User;
import blogengine.models.dto.authdto.RegisterRequest;
import blogengine.models.dto.authdto.UserLoginDto;
import blogengine.models.dto.userdto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class UserDtoMapper {

    public UserDto userToUserDto(User user) {

        UserDto userDTO = new UserDto();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setPhoto(user.getPhoto());
        return userDTO;
    }

    public UserLoginDto userToLoginDto(User user){

        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setId(user.getId());
        loginDto.setName(user.getName());
        loginDto.setPhoto(user.getPhoto());
        loginDto.setEmail(user.getEmail());
        loginDto.setModeration(false);
        loginDto.setModerationCount(0);
        loginDto.setSettings(true);
        return loginDto;
    }

    public User registerDtoToUser(RegisterRequest request){
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRegTime(LocalDateTime.now());
        user.setModerator(false);
        return user;
    }
}
