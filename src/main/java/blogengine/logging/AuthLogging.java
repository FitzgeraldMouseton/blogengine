package blogengine.logging;

import blogengine.models.dto.authdto.AuthenticationResponse;
import blogengine.models.dto.authdto.LoginRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class AuthLogging {

//    @Before(value = "execution(* (@org.springframework.stereotype.Service *).*(..))")
//    public void enteringService(JoinPoint joinPoint) {
//        log.info("Entering: " + joinPoint.getStaticPart().getSignature());
//    }

    @AfterReturning(pointcut = "execution(* blogengine.services.AuthService.login(..))",
            returning = "result")
    public void login(JoinPoint joinPoint, Object result) {
        // 1 способ
        Object[] args = joinPoint.getArgs();
        LoginRequest loginRequest = (LoginRequest) args[0];

        // 2 способ
        AuthenticationResponse response = (AuthenticationResponse) result;
        log.info("User: " + response.getUser().getEmail() + " logged in");
    }

//    @AfterReturning(pointcut = "execution(* blogengine.services.AuthService.setNewPassword(..))",
//            returning = "result")
//    public void restorePassword(JoinPoint joinPoint, Object result) {
//        AuthenticationResponse response = (AuthenticationResponse) result;
//        log.info("User: " + response.getUser().getEmail() + " logged in");
//    }
}
