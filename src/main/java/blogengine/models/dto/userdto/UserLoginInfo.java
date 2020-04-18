package blogengine.models.dto.userdto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserLoginInfo {

    private boolean result = true;
    private UserLoginDto user;
}
