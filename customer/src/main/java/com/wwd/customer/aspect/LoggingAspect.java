package com.wwd.customer.aspect;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Copyright: Copyright (c) 2025 Asiainfo
 *
 * @ClassName: com.wwd.customer.aspect.LoggingAspect
 * @Description:
 * @version: v1.0.0
 * @author: wangwd7
 * @date: 2025-11-19
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2025-11-19     wangwd7          v1.0.0               创建
 */
@Component
@Aspect
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    // 切点：Controller包下的所有方法
    @Pointcut("execution(* com.wwd.customer.controller.*.*(..))")
    public void controllerPointcut() {}

    // 切点：Service包下的所有方法
    @Pointcut("execution(* com.wwd.customer.service.*.*(..))")
    public void servicePointcut() {}

    @Around("controllerPointcut()")
    public Object logControllerExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - start;

            logger.info("{}.{} executed in {} ms",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    executionTime);
            return result;
        } catch (Exception e) {
            logger.error("Error in {}.{} : {}",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    e.getMessage());

            throw e;
        }
    }

    // 前置通知：记录Service方法调用
    @Before("servicePointcut()")
    public void logServiceMethodCall() {
        // 检查是否有请求上下文
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            // 如果没有请求上下文（比如在消息监听线程中），直接返回
            return;
        }

        // 原有的日志记录逻辑...
        ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
        HttpServletRequest request = attributes.getRequest();
        logger.info("Service method called from: {}", request.getRequestURL());
    }

    @AfterReturning(pointcut = "controllerPointcut()", returning = "result")
    public void logAfterReturning(Object result) {
        logger.info("Method returned:{}", result);
    }

}
