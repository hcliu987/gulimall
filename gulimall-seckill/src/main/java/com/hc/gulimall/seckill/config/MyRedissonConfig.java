package com.hc.gulimall.seckill.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyRedissonConfig {
    /**
     * 所有对redisson 的使用都是通过redissonclient
     */
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient(){
        //创建配置
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        //2，根据config 创建出RedissonClient实例
        RedissonClient redissonClient = Redisson.create();
        return redissonClient;
    }
}
