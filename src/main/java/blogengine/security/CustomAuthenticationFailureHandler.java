package blogengine.security;

import blogengine.models.dto.ErrorResponse;
import blogengine.models.dto.SimpleResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        log.info("CustomAuthenticationFailureHandler: " + e.getMessage());
        response.setStatus(HttpStatus.OK.value());
        ErrorResponse errorResponse = new ErrorResponse(Collections.emptyMap());
        response.getOutputStream()
                .println(objectMapper.writeValueAsString(new SimpleResponseDto(false)));
    }
}