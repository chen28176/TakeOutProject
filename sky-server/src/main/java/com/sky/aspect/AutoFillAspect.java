package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面，实现公共字段自动填充处理逻辑
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    @Pointcut("@annotation(com.sky.annotation.AutoFill)")
    public void pt(){}

    @Before("pt()")
    public void autoFill(JoinPoint joinPoint){
        log.info("-------公共字段填充切点测试--------");

        // 1. 获取连接点的参数 -- 实体对象
        Object[] args = joinPoint.getArgs();
        // 2.1 如果实体参数为空，则执行结束
        if (args == null || args.length == 0){
            return;
        }
        // 2.2 如果实体参数不为空，则获取第一个参数（这个要求编写代码规范中，第一个参数总是实体参数）
        Object arg = args[0];

        //准备赋值的数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        // 2. 根据操作类型（新增还是更新）对实体参数进行动态赋值（获取方法上的注解，通过注解的value来判断操作类型）
        // 2.1. 获取到当前被拦截的方法上的数据库操作类型(新增/更新)
        // 2.1.1 获取连接点方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 2.1.2 获取连接点方法上的注解AutoFill
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        // 2.1.3 获取AutoFill注解的value值，获取操作类型
        OperationType operationType = autoFill.value();

        // 2.2 拿到实体的字节码对象
        // 2.3 通过反射获取setXxxx方法对象
        try {
            if (operationType.equals(OperationType.INSERT)) {
                Method setCreateTime = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                // 2.4 通过反射为对象属性赋值（invoke（））
                setCreateTime.invoke(arg,now);
                setCreateUser.invoke(arg,currentId);
                setUpdateTime.invoke(arg,now);
                setUpdateUser.invoke(arg,currentId);
            }

            if (operationType.equals(OperationType.UPDATE)){
                Method setUpdateTime = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                setUpdateTime.invoke(arg,now);
                setUpdateUser.invoke(arg,currentId);

            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}