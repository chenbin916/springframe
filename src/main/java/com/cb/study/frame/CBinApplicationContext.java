package com.cb.study.frame;

import com.cb.study.annotation.Autowired;
import com.cb.study.annotation.Component;
import com.cb.study.annotation.ComponentScan;
import com.cb.study.annotation.Scope;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class CBinApplicationContext {

    private ConcurrentHashMap<String,BeanDefinition> beandefinitionMap=new ConcurrentHashMap<String,BeanDefinition>();
    private ConcurrentHashMap<String,Object> singletons=new ConcurrentHashMap<String,Object>();
    List<BeanPostProcessor> beanPostProcessors=new ArrayList<BeanPostProcessor>();


    public CBinApplicationContext(Class configClass) {
        scanPackageClass(configClass);

        //循环创建所有单例bean
        for(String beanName:beandefinitionMap.keySet()){
            BeanDefinition beanDefinition=beandefinitionMap.get(beanName);
           Object bean= createBean(beanName,beanDefinition);
            if("singleton".equalsIgnoreCase(beanDefinition.getScope()))
            {
                singletons.put(beanName,bean);
            }
        }
        ;




    }




    public void scanPackageClass(Class configClass){

        //扫描类
        //生成单例非延时加载的Bean
        if(!configClass.isAnnotationPresent(ComponentScan.class))
        {
            System.out.println("未发现配置类。。。，请检查 ");
            return ;
        }
        ComponentScan scan = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
        String scanPath=scan.value();
      //  System.out.println("scanPath:"+scanPath);
        scanPath=scanPath.replace(".","/");
      //  System.out.println(scanPath);
        ClassLoader classLoader = this.getClass().getClassLoader();
        URL resource  = classLoader.getResource(scanPath);
        File folder= new File(resource.getFile());
       // System.out.println(folder);
        //指定目录下可能是class,或txt等其他文件。
        File []files=folder.listFiles();
        for(File f:files){
            // System.out.println(f);

            if(f.getName().endsWith(".class"))
            {

                try {
                    String tmpfilename=f.getPath();
                    //扫描路径可能有多个，所以取出类的绝对路径，并把斜杠"/"转成点".",classLoader加载类时不用带扩展名.class，
                    //所以要把扩展名去掉。
                    tmpfilename=tmpfilename.substring(tmpfilename.indexOf("com"),tmpfilename.indexOf(".class"));
                    tmpfilename=tmpfilename.replace("\\",".");
                   // System.out.println(tmpfilename);
                    Class clazz= classLoader.loadClass(tmpfilename);
                    if(clazz.isAnnotationPresent(Component.class));//判断注释是否为Component类型。
                    {
                        Component comAnnotation = (Component) clazz.getAnnotation(Component.class);
                        String beanName=comAnnotation.value();
                        //System.out.println("beanName:"+beanName);
                        BeanDefinition beanDefinition=new BeanDefinition();
                        beanDefinition.setClazz(clazz);
                        if(clazz.isAnnotationPresent(Scope.class)) {
                            Scope scope = (Scope) clazz.getAnnotation(Scope.class);
                            beanDefinition.setScope(scope.value());
                        }
                        else
                            beanDefinition.setScope("singleton");

                        if(BeanPostProcessor.class.isAssignableFrom(clazz))//是否有后置处理器
                        {
                            Object postBean = clazz.getDeclaredConstructor().newInstance();
                            beanPostProcessors.add((BeanPostProcessor) postBean);
                        }


                        beandefinitionMap.put(beanName,beanDefinition);


                    }
                } catch (ClassNotFoundException | NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }


            }

        }

    }

    private Object createBean(String beanName,BeanDefinition beanDefinition) {
 //Spring Bean生命周期为：1.实例化Bean.2.填充类属性，如静态变量。3.调用后置处理（初始化前）4.初始化
        //     5.调用后置处理（初始化后）。6.处理Aware接口。

            //开始生成Bean
            Class clazz=beanDefinition.getClazz();
            Object bean=null;

            try {
                //实例化bean
                 bean=clazz.getDeclaredConstructor().newInstance();
                //填充属性
                Field [] fields =clazz.getDeclaredFields();
                for(Field field:fields){
                      if(field.isAnnotationPresent(Autowired.class))
                      {
                          Object userService=getBean(field.getName());
                          field.setAccessible(true);
                          field.set(bean,userService);
                      }

                }
                //后置处理器，初始化前
               for(BeanPostProcessor postBean:beanPostProcessors){
                   postBean.postProcessBeforeInitialization(bean,beanName);
               }

                //初始化bean
                if(bean instanceof InitialzingBean)
                {
                    ((InitialzingBean)bean).afterPropertiesSet();
                }


                //后置处理器，初始化后
                for(BeanPostProcessor postBean:beanPostProcessors){
                    postBean.postProcessAfterInitialization(bean,beanName);
                }


                //调用aware
                if(bean instanceof BeanAware)
                {
                    ((BeanAware)bean).setBeanName(beanName);
                }


            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        return bean;
        }


    public Object getBean(String beanName){

        Object res;
        BeanDefinition beanDefinition=beandefinitionMap.get(beanName);
        if("singleton".equalsIgnoreCase(beanDefinition.getScope()))
        {
            res=singletons.get(beanName);
            if(null==res)//未解决循环依赖问题
            {
                res=createBean(beanName, beanDefinition);
                singletons.put(beanName,res);
            }
        }else  //原型
           res= createBean( beanName,beanDefinition);


        return res;
    }
}
