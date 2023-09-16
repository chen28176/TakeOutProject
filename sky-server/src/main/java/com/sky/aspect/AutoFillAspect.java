package com.sky.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.Joinpoint;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @author Chen
 * @date 2023/9/16
 * @apiNote
 * 自定义切片，实现公共字段自动填充处理逻辑
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    /**
     * 切入点
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPonintCut(){}

    /**
     * 前置通知，在通知中进行公共字段负责
     */
    @Before("autoFillPonintCut()")
    public void autoFill(JoinPoint joinpoint){
        log.info("开始进行公共字段的自动填充");

        // todo 字段填充代码编写
    }
}
