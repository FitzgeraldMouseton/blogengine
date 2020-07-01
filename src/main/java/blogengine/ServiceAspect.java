package blogengine;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
//@Component
public class ServiceAspect {

    @Before(value = "execution(* (@org.springframework.stereotype.Service *).*(..))")
    public void entering(JoinPoint joinPoint) {
        System.out.println("Entering: " + joinPoint.getStaticPart().getSignature());
    }

    @AfterThrowing(pointcut = "execution(* (@org.springframework.stereotype.Service *).*(..))", throwing = "ex")
    public void logRuntimeException(RuntimeException ex) {

        log.error("Runtime error: " +  ex.toString());
    }
}