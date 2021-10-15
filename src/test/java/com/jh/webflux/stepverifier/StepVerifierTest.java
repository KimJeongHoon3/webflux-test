package com.jh.webflux.stepverifier;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalUnit;

public class StepVerifierTest {
    @Test
    void testStepVerifier(){
        Flux<String> just = Flux.just("Apple", "Orange", "Grape", "Banana");
        long seconds=StepVerifier.create(just)
                .expectNext("Apple", "Orange", "Grape", "Banana")
                .verifyComplete().getSeconds();
        System.out.println(seconds);
    }
}
