package blogengine.security;

import blogengine.mappers.UserDtoMapper;
import blogengine.models.User;
import blogengine.models.dto.authdto.AuthenticationResponse;
import blogengine.models.dto.authdto.UserLoginResponse;
import blogengine.util.SessionStorage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@AllArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final SessionStorage sessionStorage;
    private final UserDtoMapper userDtoMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        response.setStatus(HttpStatus.OK.value());
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        User user = (User) authentication.getPrincipal();
//        user.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.toString()));
        Integer userId = user.getId();
        sessionStorage.getSessions().put(sessionId, userId);
        UserLoginResponse loginDto = userDtoMapper.userToLoginResponse(user);
        AuthenticationResponse authenticationResponse = new AuthenticationResponse(true, loginDto);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().println(objectMapper.writeValueAsString(authenticationResponse));
    }
}
