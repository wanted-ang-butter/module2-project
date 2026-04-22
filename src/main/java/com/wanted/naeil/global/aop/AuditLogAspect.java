package com.wanted.naeil.global.aop;

import com.wanted.naeil.global.aop.annotation.AuditLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class AuditLogAspect {

    @AfterReturning("@annotation(auditLog)")
    public void auditSuccess(JoinPoint joinPoint, AuditLog auditLog) {
        log.info("[AUDIT_SUCCESS] action={}, method={}.{}, args={}",
                auditLog.action(),
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()));
    }

    @AfterThrowing(pointcut = "@annotation(auditLog)", throwing = "ex")
    public void auditFailure(JoinPoint joinPoint, AuditLog auditLog, Exception ex) {
        log.warn("[AUDIT_FAILURE] action={}, method={}.{}, args={}, reason={}",
                auditLog.action(),
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()),
                ex.getMessage());
    }
}
