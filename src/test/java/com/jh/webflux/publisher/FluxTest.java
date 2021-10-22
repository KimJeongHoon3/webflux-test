package com.jh.webflux.publisher;

import io.reactivex.Observable;
import io.reactivex.Observer;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class FluxTest {
    @Test
    void mergeFluxes(){
        Flux<String> flux1 = Flux.just("Garfield", "Kojak", "Barbossa")
                .delayElements(Duration.ofSeconds(1));

        Flux<String> flux2 = Flux.just("Garfield2", "Kojak2", "Barbossa2")
                .delaySubscription(Duration.ofMillis(500))
                .delayElements(Duration.ofSeconds(1));

        Flux<String> fluxes = flux1.mergeWith(flux2);

        StepVerifier.create(fluxes)
                .expectNext("Garfield","Garfield2", "Kojak","Kojak2", "Barbossa","Barbossa2")
                .verifyComplete();
    }

    @Test
    void zipFluxes(){
        Flux<String> flux1 = Flux.just("Garfield", "Kojak", "Barbossa")
                .delayElements(Duration.ofSeconds(1));

        Flux<String> flux2 = Flux.just("Garfield2", "Kojak2", "Barbossa2")
                .delayElements(Duration.ofSeconds(1));

        Flux<Tuple2<String, String>> zip = Flux.zip(flux1, flux2);

        StepVerifier.create(zip)
                .expectNextMatches(tuple->tuple.getT1().equals("Garfield") && tuple.getT2().equals("Garfield2"))
                .expectNextMatches(tuple->tuple.getT1().equals("Kojak") && tuple.getT2().equals("Kojak2"))
                .expectNextMatches(tuple->tuple.getT1().equals("Barbossa") && tuple.getT2().equals("Barbossa2"))
                .verifyComplete();
    }

    @Test
    void zipFluxes2(){
        Flux<String> flux1 = Flux.just("Garfield", "Kojak", "Barbossa")
                .delayElements(Duration.ofSeconds(1));

        Flux<String> flux2 = Flux.just("Garfield2", "Kojak2", "Barbossa2")
                .delayElements(Duration.ofSeconds(1));
        Flux<String> zip = Flux.zip(flux1, flux2
                , (f1,f2) -> f1+" | "+f2);

        StepVerifier.create(zip)
                .expectNext("Garfield | Garfield2")
                .expectNext("Kojak | Kojak2")
                .expectNext("Barbossa | Barbossa2")
                .verifyComplete();

    }

    @Test
    void delay() throws InterruptedException {
        Observable
                .timer(1, TimeUnit.SECONDS)
                .flatMap(i -> Observable.just("1","2","3"))
                .subscribe(System.out::println);

        Thread.sleep(5000);
    }

    @Test
    void delay2() throws InterruptedException {
        Observable.just("Lorem","ipsum","dolor","sit","amet","acbcfsdfsd","qweqwe","elit")
                .flatMap(s->Observable.timer(s.length(),TimeUnit.SECONDS).map(l -> s))
                .subscribe(System.out::println);
        Thread.sleep(15000);
    }
}
