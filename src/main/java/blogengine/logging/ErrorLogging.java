package blogengine.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
//@Component
public class ErrorLogging {

    @AfterThrowing(pointcut = "execution(* blogengine.services.AuthService.login(..))", throwing = "ex")
    public void logRuntimeException(JoinPoint joinPoint, RuntimeException ex) {

        log.info("Runtime error: " +  ex.getLocalizedMessage() +
                    " in " + joinPoint.getSignature());
        log.info("Runtime error: " +  ex.getCause());
    }
}
