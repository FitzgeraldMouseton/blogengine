package blogengine.mappers;

import blogengine.models.User;
import blogengine.models.dto.userdto.LoginDto;
import blogengine.models.dto.userdto.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class UserDtoMapper {

    public UserDTO userToUserDto(User user) {

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        return userDTO;
    }

    public LoginDto userToLoginDto(User user){

        LoginDto loginDto = new LoginDto();
        loginDto.setId(user.getId());
        loginDto.setName(user.getName());
        loginDto.setPhoto(user.getPhoto());
        loginDto.setModeration(false);
        loginDto.setModerationCount(0);
        loginDto.setSettings(true);
        return loginDto;
    }
}
