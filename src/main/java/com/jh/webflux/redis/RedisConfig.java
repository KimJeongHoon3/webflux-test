package com.jh.webflux.redis;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Bean
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
        return new LettuceConnectionFactory("127.0.0.1", 6379);
    }

    @Bean
    public ReactiveRedisTemplate<String,User> reactiveRedisTemplate(){
        StringRedisSerializer key=new StringRedisSerializer();
        Jackson2JsonRedisSerializer<User> valueSerializer=new Jackson2JsonRedisSerializer<>(User.class);
        RedisSerializationContext<String, User> context = RedisSerializationContext.<String, User>newSerializationContext(key)
                .value(valueSerializer)
                .build();
        return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory(),context);
    }

//    @Bean
//    public ReactiveRedisTemplate<String,String> reactiveRedisTemplateString(@Qualifier("customReactiveRedisConnectionFactory") ReactiveRedisConnectionFactory factory){
//        StringRedisSerializer key=new StringRedisSerializer();
//        StringRedisSerializer value=new StringRedisSerializer();
//        RedisSerializationContext<String, String> context = RedisSerializationContext.<String, String>newSerializationContext(key)
//                .value(value)
//                .build();
//        return new ReactiveRedisTemplate<>(factory,context);
//    }


}
