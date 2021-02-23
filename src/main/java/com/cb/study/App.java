package com.cb.study;

import com.cb.study.config.Config;
import com.cb.study.frame.CBinApplicationContext;
import com.cb.study.service.OrderService;
import com.cb.study.service.UserService;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        CBinApplicationContext context = new CBinApplicationContext(Config.class);

        //单例、原型测试
        /**
        System.out.println( "获取bean orderService："+(OrderService) context.getBean("orderService"));
        System.out.println( "获取bean orderService："+(OrderService) context.getBean("orderService"));
       ((OrderService) context.getBean("orderService")).testGetBean();;


        System.out.println( "获取bean userService："+(UserService) context.getBean("userService"));
        System.out.println( "获取bean userService："+(UserService) context.getBean("userService"));
        System.out.println( "获取bean userService："+(UserService) context.getBean("userService"));
         **/

        context.getBean("orderService");


    }



}
