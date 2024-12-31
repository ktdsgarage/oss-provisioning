// common/src/main/java/com/oss/common/aspect/LoggingAspect.java
package com.oss.common.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Pointcut("within(@org.springframework.stereotype.Service *)" +
            " || within(@org.springframework.stereotype.Component *)" +
            " || within(@org.springframework.web.bind.annotation.RestController *)")
    public void springBeanPointcut() {}

    @Pointcut("within(com.oss..*)" +
            " && !within(com.oss.common.aspect..*)")
    public void applicationPackagePointcut() {}

    @Before("springBeanPointcut() && applicationPackagePointcut()")
    public void logMethodStart(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String args = Arrays.toString(joinPoint.getArgs());
        if(args.length() > 500) {
            args = args.substring(0, 500) + "...";
        }

        log.info("==> [{}] {}.{} Start - Args: {}",
                Thread.currentThread().getId(),
                className,
                methodName,
                args);
    }

    @AfterReturning(pointcut = "springBeanPointcut() && applicationPackagePointcut()", returning = "result")
    public void logMethodEnd(JoinPoint joinPoint, Object result) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String returnValue = result != null ? result.toString() : "void";
        if(returnValue.length() > 500) {
            returnValue = returnValue.substring(0, 500) + "...";
        }

        log.info("<== [{}] {}.{} End - Result: {}",
                Thread.currentThread().getId(),
                className,
                methodName,
                returnValue);
    }

    @AfterThrowing(pointcut = "springBeanPointcut() && applicationPackagePointcut()", throwing = "exception")
    public void logMethodException(JoinPoint joinPoint, Exception exception) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        log.error("xxx [{}] {}.{} Exception - Message: {}",
                Thread.currentThread().getId(),
                className,
                methodName,
                exception.getMessage(),
                exception);
    }
}
