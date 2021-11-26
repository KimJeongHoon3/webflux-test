package com.jh.webflux.redis;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
public class ReactiveRedisTest {
    @Autowired
    ReactiveRedisTemplate<String,User> reactiveRedisTemplate;

    @Autowired
    ReactiveStringRedisTemplate reactiveStringRedisTemplate;
//    @BeforeEach
//    public void setup(){
//
//    }

    @Test
    void stringsTest(){

        User u = new User("30001", "kim");
        reactiveRedisTemplate.opsForValue().set("key_user",u).subscribe();


        Mono<User> m = reactiveRedisTemplate.opsForValue().get("key_user");

        StepVerifier.create(m)
                .expectNext(u)
                .verifyComplete();

        Mono<String> m2 = reactiveStringRedisTemplate.opsForValue().get("key_user");
        StepVerifier.create(m2)
                .consumeNextWith(System.out::println)
                .verifyComplete();

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
