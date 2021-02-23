package com.cb.study.service;

import com.cb.study.annotation.Component;
import com.cb.study.frame.BeanPostProcessor;

@Component
public class ChenBinBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {

        System.out.println("postProcessBeforeInitialization invoked..");
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.println("postProcessAfterInitialization invoked..");
        return bean;
    }
}
