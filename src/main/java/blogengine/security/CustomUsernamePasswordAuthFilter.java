package blogengine.security;

import blogengine.models.dto.authdto.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@AllArgsConstructor
public class CustomUsernamePasswordAuthFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        final ObjectMapper objectMapper = new ObjectMapper();
        String requestBody;
        try {
            requestBody = IOUtils.toString(request.getReader());
            LoginRequest authRequest = objectMapper.readValue(requestBody, LoginRequest.class);

            log.info("filter:" + authRequest.getEmail());
            log.info("filter:" + authRequest.getPassword());

            UsernamePasswordAuthenticationToken token
                    = new UsernamePasswordAuthenticationToken(authRequest.getEmail(),
                                                                authRequest.getPassword());

            setDetails(request, token);

            Authentication authentication = this.getAuthenticationManager().authenticate(token);
            log.info("Custom filter: " + authentication);
            log.info("Custom filter: " + (this.getAuthenticationManager().toString()));
            return authentication;
        } catch(IOException e) {
            throw new InternalAuthenticationServiceException("jk", e);
        }
    }
}
