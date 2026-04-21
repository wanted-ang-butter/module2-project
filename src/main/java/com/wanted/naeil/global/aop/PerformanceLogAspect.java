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

        long startTime = System.nanoTime();

        try {
            return joinPoint.proceed();
        } finally {
            long endTime = System.nanoTime();

            long executionTimeMs = (endTime - startTime) / 1_000_000;
            double executionTimeSec = (endTime - startTime) / 1_000_000_000.0;

            MethodSignature signature = (MethodSignature) joinPoint.getSignature();

            log.info("""
                
                ==================== [성능 측정 시작] ====================
                대상 메서드 : {}.{}
                실행 시간  : {}ms
                실행 시간  : {}초
                ==================== [성능 측정 종료] ====================
                """,
                    signature.getDeclaringType().getSimpleName(),
                    signature.getMethod().getName(),
                    executionTimeMs,
                    String.format("%.2f", executionTimeSec)
            );
        }
    }
}
