package com.jh.webflux.router;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class Router {
//    HandlerFunction handlerFunction=request -> {
//        String name=request.pathVariable("name");
//        return ServerResponse.ok().body(Mono.just("Hello"+name),String.class);
//    };

    HandlerFunction handlerFunction=request -> ok().bodyValue("Hello "+request.pathVariable("name"));

    RouterFunction routerFunction=request ->
        path("/hello/{name}").test(request) ? Mono.just(handlerFunction) : Mono.empty();

    RouterFunction goodRouterFunction= route(path("/hello/{name}")
            ,request -> ok().bodyValue("Hello "+request.pathVariable("name")));

    @Bean
    RouterFunction helloRf(){

        return route(path("/hello/{name}"),handlerFunction);
    }
}
