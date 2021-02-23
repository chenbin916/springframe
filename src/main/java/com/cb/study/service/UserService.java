package com.cb.study.service;

import com.cb.study.annotation.Component;
import com.cb.study.annotation.Scope;
import com.cb.study.frame.BeanAware;
import com.cb.study.frame.InitialzingBean;

@Component("userService")
@Scope("prototype")
public class UserService  implements InitialzingBean, BeanAware {

    private  String beanName;


    @Override
    public void afterPropertiesSet()  {
        System.out.println("UserService 初始化完成。。。");//原型模式会多次初始化
    }

    @Override
    public void setBeanName(String beanName) {

     this.beanName=beanName;
    }
}
