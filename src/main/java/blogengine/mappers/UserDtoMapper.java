package blogengine.mappers;

import blogengine.models.User;
import blogengine.models.dto.userdto.UserLoginDto;
import blogengine.models.dto.userdto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserDtoMapper {

    public UserDTO userToUserDto(User user) {

        UserDTO userDTO = new UserDTO();
        log.info(user.toString());
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
}
