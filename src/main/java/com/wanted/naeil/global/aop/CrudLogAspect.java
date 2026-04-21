package com.wanted.naeil.global.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class CrudLogAspect {

    // 생성 AOP
    @AfterReturning("""
        execution(* com.wanted.naeil.domain..service..create*(..)) ||
        execution(* com.wanted.naeil.domain..service..register*(..)) ||
        execution(* com.wanted.naeil.domain..service..add*(..))
    """)
    public void logCreateSuccess(JoinPoint joinPoint) {
        logSuccess("CREATE", joinPoint);
    }

    // 수정 AOP
    @AfterReturning("""
        execution(* com.wanted.naeil.domain..service..update*(..)) ||
        execution(* com.wanted.naeil.domain..service..modify*(..))
    """)
    public void logUpdateSuccess(JoinPoint joinPoint) {
        logSuccess("UPDATE", joinPoint);
    }

    // 삭제 AOP
    @AfterReturning("""
        execution(* com.wanted.naeil.domain..service..delete*(..)) ||
        execution(* com.wanted.naeil.domain..service..cancel*(..))
    """)
    public void logDeleteSuccess(JoinPoint joinPoint) {
        logSuccess("DELETE", joinPoint);
    }

    // 취소 성공 AOP
    @AfterReturning("execution(* com.wanted.naeil.domain..service..cancel*(..))")
    public void logCancelSuccess(JoinPoint joinPoint) {
        logSuccess("CANCEL", joinPoint);
    }

    // 전체 실패 AOPㅠ
    @AfterThrowing(
            pointcut = """
                execution(* com.wanted.naeil.domain..service..create*(..)) ||
                execution(* com.wanted.naeil.domain..service..register*(..)) ||
                execution(* com.wanted.naeil.domain..service..add*(..)) ||
                execution(* com.wanted.naeil.domain..service..update*(..)) ||
                execution(* com.wanted.naeil.domain..service..modify*(..)) ||
                execution(* com.wanted.naeil.domain..service..delete*(..)) ||
                execution(* com.wanted.naeil.domain..service..cancel*(..))
            """,
            throwing = "ex"
    )
    public void logCrudFailure(JoinPoint joinPoint, Exception ex) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        log.warn("[CRUD_실패] {}.{} failed. reason={}",
                signature.getDeclaringType().getSimpleName(),
                signature.getMethod().getName(),
                ex.getMessage());
    }

    // ==== 내부 편의 메서드 ====
    private void logSuccess(String action, JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        log.info("[{}] {}.{} 메서드 실행 성공",
                action,
                signature.getDeclaringType().getSimpleName(),
                signature.getMethod().getName());
    }
}
