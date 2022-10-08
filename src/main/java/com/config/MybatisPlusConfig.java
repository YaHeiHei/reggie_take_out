package com.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration  //@Configuration此注解设置此类为一个配置类，以便让Spring读到
public class MybatisPlusConfig {
    /**
     * 配置mybatis_plus拦截器
     * @return
     */
    @Bean  //@Bean此注解用来配置第三方bean让spring管理
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();  //new一个mybatis_plus的拦截器
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor()); //new一个分页拦截器添加到mybatis_plus的拦截器中
        return mybatisPlusInterceptor;  //返回mybatis_plus的拦截器
    }
}