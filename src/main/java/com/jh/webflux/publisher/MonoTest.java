package com.jh.webflux.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import reactor.core.publisher.Mono;

@Slf4j
public class MonoTest {

    public static void main(String[] args) {


        StopWatch stopWatch=new StopWatch();
        stopWatch.start();
        map();
        stopWatch.stop();
        stopWatch.start();
//        flatmap();
        map2();
        stopWatch.stop();

        log.info("map total : "+(stopWatch.getTotalTimeMillis()-stopWatch.getLastTaskTimeMillis()));
        log.info("flatmap total : "+(stopWatch.getLastTaskTimeMillis()));
    }

    private static void map() {
        Mono.just(1)
                .map(i ->{

                    log.info("map1 : "+i);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return i+"| hi";
                })
                .map(str ->{
                    log.info("map2 : "+str);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return str+"| hi2";
                })
                .subscribe((str) -> log.info("map 완료"));
    }

    private static void map2() {
        Mono.just(1)
                .map(i ->{

                    log.info("map1 : "+i);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String str= i+"| hi";

                    log.info("map2 : "+str);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return str+"| hi2";
                })
                .subscribe((str) -> log.info("map 완료"));
    }

    private static void flatmap() {
        Mono.just(1)
                .flatMap(i ->{


                    return Mono.fromSupplier(()->{
                        log.info("map1 : "+i);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return i+"| hi";
                    });
                })
                .flatMap(str ->{
                    return Mono.fromSupplier(()->{
                        log.info("map2 : "+str);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return str+"| hi2";
                    });
                })
                .subscribe(str ->{
                    log.info("flatmap 완료");
                });
    }
}
