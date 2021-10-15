package com.jh.webflux.publisher;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

import java.time.Duration;

@Slf4j
public class FluxOperation {
    public static void main(String[] args) throws InterruptedException {
//        Flux<String> flux1 = Flux.just("Garfield", "Kojak", "Barbossa")
//                .delayElements(Duration.ofSeconds(1));
//
//        Flux<String> flux2 = Flux.just("Garfield2", "Kojak2", "Barbossa2")
////                .delaySubscription(Duration.ofMillis(500))
//                .delayElements(Duration.ofSeconds(1));
//        flux1.mergeWith(flux2)
//                .subscribe(log::info);

//        Flux<String> flux1 = Flux.just("Garfield", "Kojak", "Barbossa")
//                .delayElements(Duration.ofSeconds(1));
//
//        Flux<String> flux2 = Flux.just("Garfield2", "Kojak2", "Barbossa2")
////                .delaySubscription(Duration.ofMillis(500))
//                .delayElements(Duration.ofSeconds(1));
//        Flux<Tuple2<String, String>> zip = Flux.zip(flux1, flux2);
//        zip.subscribe(tuple -> log.info(tuple.getT1()+" , "+tuple.getT2()));

//        Flux<String> flux1 = Flux.just("Garfield", "Kojak", "Barbossa")
//                .delayElements(Duration.ofSeconds(1));
//
//        Flux<String> flux2 = Flux.just("Garfield2", "Kojak2", "Barbossa2")
//                .delayElements(Duration.ofSeconds(1));
//        Flux<String> zip = Flux.zip(flux1, flux2
//                , (f1,f2) -> f1+" | "+f2);
//        zip.subscribe(log::info);
        Flux.just("마이클 조던","스카티 피팬","스테판 커리")
                .flatMap(n -> Mono.just(n)
                        .map(p -> {
                            log.info("msg : "+p);
                            String[] split=p.split("\\s");
                            return split[0]+" - "+split[1];
                        }).log("map임").subscribeOn(Schedulers.parallel()).log("subscrieOn임")
                ).log("flatMap임")
                .subscribe(log::info);

        Thread.sleep(10000);

    }
}
