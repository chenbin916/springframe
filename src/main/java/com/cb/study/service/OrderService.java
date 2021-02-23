package com.cb.study.service;


import com.cb.study.annotation.Autowired;
import com.cb.study.annotation.Component;
import com.cb.study.frame.BeanAware;
import com.cb.study.frame.BeanPostProcessor;
import com.cb.study.frame.InitialzingBean;

@Component("orderService")
public class OrderService implements InitialzingBean, BeanAware, BeanPostProcessor {

    @Autowired
    private  UserService userService;

    private  String beanName;

    public void testGetBean(){

        System.out.println("output by testGetBean@OrderService,userService is :"+userService);
        System.out.println("output by testGetBean@OrderService,beanName is :"+beanName);

    }

    @Override
    public void afterPropertiesSet()  {
        System.out.println("OrderService 初始化完成。。。");
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName=beanName;

    }

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
