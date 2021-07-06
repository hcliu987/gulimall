package com.hc.gulimall.ware.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@MapperScan("com.hc.gulimall.ware.dao")
public class MyBatisConfig {
    //引入分页插件
    @Bean
    public PaginationInterceptor paginationInterceptor(){
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        //设置请求的页面页面大雨最大页后操作，true回掉到首页 false 继续请求 默认false
        paginationInterceptor.setOverflow(true);
        //设置最大页面数量 默认500 -1 不受限制
        paginationInterceptor.setLimit(1000);
        return paginationInterceptor;
    }
}
