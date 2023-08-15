package com.coder.community.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class MQLogAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(MQLogAspect.class);

    @Pointcut("execution(* com.coder.community.util.MQ.*(..))")
    public void pointCut() {

    }
}
