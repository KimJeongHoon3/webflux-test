package com.jh.webflux.publisher;

import io.reactivex.Observable;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.TimeUnit;

@Slf4j
public class ObservableTest {
    public static void main(String[] args) throws InterruptedException {
//        delay2();
        delay3();
    }

    private static void delay3() throws InterruptedException {
        Flux.just("Lorem","ipsum","dolor","sit","amet","acbcfsdfsd","qweqwe","elit")
                .flatMap(s-> Mono.fromSupplier(()->{
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return s;
                })/*.subscribeOn(Schedulers.parallel())*/,10)
                .subscribe(log::info);

        Thread.sleep(10000);
    }

    static void delay2() throws InterruptedException {
        Observable.just("Lorem","ipsum","dolor","sit","amet","acbcfsdfsd","qweqwe","elit")
                .flatMap(s->Observable.timer(s.length(), TimeUnit.SECONDS).map(l -> s))
                .subscribe(log::info);


        Thread.sleep(15000);
    }
}
