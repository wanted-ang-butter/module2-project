package com.wanted.naeil.global.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class PerformanceLogAspect {

    // get으로 시작하는 모든 서비스 메서드
    @Around("""
    execution(* com.wanted.naeil.domain..service..get*(..)) ||
    execution(* com.wanted.naeil.domain..service..find*(..))
    """)
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        long startTime = System.currentTimeMillis();

        try {
            return joinPoint.proceed();
        } finally {
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            MethodSignature signature = (MethodSignature) joinPoint.getSignature();

            log.info("[성능 측정] {}.{} 싷행 시간 {}초",
                    signature.getDeclaringType().getSimpleName(),
                    signature.getMethod().getName(),
                    executionTime);
        }
    }
}
