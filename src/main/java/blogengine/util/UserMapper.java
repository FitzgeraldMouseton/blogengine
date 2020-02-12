package blogengine.util;

import blogengine.model.User;
import blogengine.model.dto.UserDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User userToUserDTO(UserDTO userDTO);
    UserDTO userDTOtoUser(User user);
}
