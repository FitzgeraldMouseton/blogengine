package blogengine.models.dto.userdto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginInfo {

    private boolean result = true;
    private LoginDto user;
}
