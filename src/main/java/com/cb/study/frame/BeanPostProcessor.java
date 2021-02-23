package com.cb.study.frame;

public interface BeanPostProcessor {

//Bean初始化之前
    Object postProcessBeforeInitialization(Object bean,String beanName);

    Object postProcessAfterInitialization(Object bean,String beanName);
}
