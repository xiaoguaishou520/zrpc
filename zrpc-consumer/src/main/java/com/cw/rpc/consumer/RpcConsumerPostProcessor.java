package com.cw.rpc.consumer;

import com.cw.rpc.annotation.RpcReference;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author 小怪兽
 * @Date 2021-04-02
 * 完成包含@RpcRefrence注解bean的实例化，并注册到容器中
 */
@Component
public class RpcConsumerPostProcessor implements BeanClassLoaderAware, ApplicationContextAware,BeanFactoryPostProcessor {

    private ClassLoader classLoader;

    private ApplicationContext applicationContext;

    /**
     * 用来保存解析成功的bean定义
     */
    private static Map<String,BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    /**
     * 通过BeanClassLoaderAware接口
     * 注入classloader
     * @param classLoader
     */
    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }


    /**
     * 通过ApplicationContextAware接口
     * 注入applicationContext对象
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 在bean初始化之前触发
     * @param beanFactory
     * @throws BeansException
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        //1.基于bean工厂获取所有bean定义信息
        String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();

        for (String beanDefinitionName : beanDefinitionNames) {
            //2.逐个获取bean的定义信息
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
            //3.获取bean的类名称
            String beanClassName = beanDefinition.getBeanClassName();
            if (beanClassName != null) {
                //4.根据bean的名称获取Class对象
                Class<?> clazz = ClassUtils.resolveClassName(beanClassName, classLoader);
                //5.判断类对象的属性是否包含注解@RpcReference
                //存在，就构建出对应的对象
                ReflectionUtils.doWithFields(clazz, new ReflectionUtils.FieldCallback() {
                    @Override
                    public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                        parseRpcReference(field);
                    }
                });
            }
        }
        //6.解析完毕之后，将bean注册到Spring容器中
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
        beanDefinitionMap.forEach((className,definition)->{
            //避免重复注册的问题，基于容器的上下文对象来判断当前类是否已经存在
            if (applicationContext.containsBean(className)) {
                throw new IllegalArgumentException("Spring context already has bean" + className);
            }
            //注册bean
            registry.registerBeanDefinition(className,definition);
        });
    }

    /**
     * 判断类对象的属性是否包含注解@RpcReference
     * 存在，就构建出对应的对象
     * @param field
     */
    private void parseRpcReference(Field field) {
        //1.获取当前对象的注解信息
        RpcReference annotation = AnnotationUtils.getAnnotation(field, RpcReference.class);
        if (annotation != null) {
            //说明该对象存在@RpcRefrence注解
            //2.基于RpcReferenceBean完成bean的实例化
            BeanDefinitionBuilder beanDefinitionBuilder =
                    BeanDefinitionBuilder.genericBeanDefinition(RpcReferenceBean.class);
            //3.为其成员变量赋值
            beanDefinitionBuilder.addPropertyValue("interfaceClass",field.getType());
            beanDefinitionBuilder.addPropertyValue("registryType",annotation.registryType());
            beanDefinitionBuilder.addPropertyValue("registryAddr",annotation.registryAddr());
            beanDefinitionBuilder.addPropertyValue("version",annotation.version());
            beanDefinitionBuilder.addPropertyValue("timeout",annotation.timeout());
            //4.基于属性构建完整的bean
            BeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
            //5.把这个结果保存到map中
            beanDefinitionMap.put(field.getName(),beanDefinition);
        }
    }
}
