package com.wwd.customer.aspect;


import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    @Pointcut("execution(* com.example.demo.service.*.*(..))")
    public void servicePointcut() {}



}
