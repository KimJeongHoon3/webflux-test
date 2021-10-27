package com.jh.webflux.redis;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveRedisTemplate;

@SpringBootTest
public class ReactiveRedisTest {
    @Autowired
    ReactiveRedisTemplate<String,User> reactiveRedisTemplate;

//    @BeforeEach
//    public void setup(){
//
//    }

    @Test
    void hashTest(){

        reactiveRedisTemplate.opsForValue().set("key",new User());
    }

//    static class User{
//        Vendor vendor;
//        String name;
//    }
//
//    static enum Vendor{
//        INFO("30000"), UPLUS("30001");
//        String value;
//
//        Vendor(String value){
//            this.value=value;
//        }
//
//        String getValue(){
//            return this.value;
//        }
//    }



}
