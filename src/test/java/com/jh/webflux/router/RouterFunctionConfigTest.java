package com.jh.webflux.router;

import com.jh.webflux.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.WebExceptionHandler;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;
import reactor.core.publisher.Mono;

import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.RouterFunctions.*;

class RouterFunctionConfigTest {
    User user;

    HandlerFunction handlerFunctionForGET= request -> {
        String id=request.pathVariable("id");
        user=new User("kim"+id,20,"basketball");
        return ServerResponse.ok().body(Mono.fromSupplier(()->user),User.class);
    };

    HandlerFunction handlerFunctionForGETWithError= request -> {
        String id=request.pathVariable("id");
        user=new User("kim"+id,20,"basketball");
        return ServerResponse.ok().body(
                Mono.fromSupplier(()->{
                    System.out.println("언제실행?");
                    String abc=request.queryParam("abc").get();
                    return user;
                })
                .onErrorResume( e -> Mono.error(new RuntimeException("임의에러")))
                ,User.class);
    };

    HandlerFunction handlerFunctionForGETWithError2= request -> {
        String id=request.pathVariable("id");
        user=new User("kim"+id,20,"basketball");
        if(1==1) throw new RuntimeException("일부러");
        return ServerResponse.ok().body(Mono.just(user),User.class);
    };

    HandlerFunction handlerFunctionForPOST= request -> {

        return ServerResponse.ok().body(Mono.just("hello"),String.class);
    };

//    HandlerFunction handlerFunctionForPOST2= request -> {
//        return ServerResponse.ok().body(Mono.just("hello"),String.class);
//    };


    RouterFunction helloRouterFunction=nest(RequestPredicates.path("/users"),
    nest(accept(MediaType.APPLICATION_JSON)
                    ,route().GET("/{id}",handlerFunctionForGET)
                            .GET("/{id}/error",handlerFunctionForGETWithError)
                            .GET("/{id}/error2",handlerFunctionForGETWithError2)
//                            .after()
//                            .before()
                            .build())
            .andNest(contentType(MediaType.APPLICATION_JSON),
    route().POST("",handlerFunctionForPOST)
                                   .build())
//            .filter(this::handlingError)
    );

//    RouterFunction helloRouterFunction=RouterFunctions.nest(RequestPredicates.accept(MediaType.APPLICATION_JSON)
//            ,route()
//                .path("/users"
//                        ,builder -> builder
//                                .GET("/{id}",handlerFunctionForGET)
//                                .GET("/{id}/error",handlerFunctionForGETWithError)
//                                .GET("/{id}/error2",handlerFunctionForGETWithError2)
//                                .POST("",handlerFunctionForPOST)
//                                /*.onError(e -> e instanceof RuntimeException ,(e, serverRequest) -> {
////                                    e.printStackTrace();
//                                    System.out.println("에러 찍히는곳 : "+e.getMessage());
//                                    return ServerResponse.status(HttpStatus.BAD_GATEWAY).build();
//                                })*/)
////                    .filter(this::handlingError)
//                    .before(serverRequest -> {
//                        System.out.println(Thread.currentThread().getName()+" | 동작3");
//                        return serverRequest;
//                    }).after((serverRequest, serverResponse) -> {
//                        System.out.println("코드 : "+serverResponse.statusCode());
//                        System.out.println(Thread.currentThread().getName()+" | 동작4");
//                        return serverResponse;
//                    })
//                .build());
////                .filter(this::handlingError));



    private Mono<ServerResponse> handlingError(ServerRequest request, HandlerFunction<ServerResponse> next) {
        System.out.println("동작2");
        return next.handle(request)
                .doOnNext(serverResponse -> {
                    System.out.println(serverResponse.toString());
                    System.out.println(serverResponse.statusCode());
                    System.out.println(Thread.currentThread().getName()+" | 동작5");
                })
               /* .onErrorResume(RuntimeException.class, e -> {
                    System.out.println("에러 찍히지: "+e.getMessage());
                    return ServerResponse.badRequest().build();
                })*/;
    }


    WebTestClient webTestClient = WebTestClient.bindToRouterFunction(helloRouterFunction)
//            .webFilter((exchange, chain) -> {
//                System.out.println(Thread.currentThread().getName()+" | 동작1");
//                return chain.filter(exchange)
//                        .onErrorResume(RuntimeException.class, e -> {
//                            System.out.println(Thread.currentThread().getName()+" | 동작6");
//                            ServerHttpResponse response = exchange.getResponse();
//                            response.setStatusCode(HttpStatus.BAD_GATEWAY);
//                            return response.setComplete();
//                        });
//            })
            .handlerStrategies(HandlerStrategies.builder()
                    .exceptionHandler((exchange, ex) -> {
                        if(ex instanceof RuntimeException){
                            exchange.getResponse().setStatusCode(HttpStatus.BAD_GATEWAY);

                            return exchange.getResponse().setComplete();
                        }
                        return Mono.error(ex);
                    }).build())
            .build();

    @Test
    void testGetMethodWithWebTestClient(){
        webTestClient.get().uri("/users/1").accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$").isNotEmpty()
        .jsonPath("$.name").isEqualTo(user.getName())
        .jsonPath("$.age").isEqualTo(user.getAge())
        .jsonPath("$.hobby").isEqualTo(user.getHobby())
        .consumeWith(System.out::println);

        webTestClient.get().uri("/users/2").accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isNotEmpty()
                .jsonPath("$.name").isEqualTo(user.getName())
                .jsonPath("$.age").isEqualTo(user.getAge())
                .jsonPath("$.hobby").isEqualTo(user.getHobby())
                .consumeWith(System.out::println);
    }

    @Test
    void GET_에러발생(){

        webTestClient.get().uri("/users/1/error").accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .consumeWith(System.out::println);
    }

    @Test
    void GET_에러2발생(){
        webTestClient.get().uri("/users/1/error").accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .consumeWith(System.out::println);

        webTestClient.get().uri("/users/1/error2").accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .consumeWith(System.out::println);

        /*
         1. 에러를 핸들링하기위해서는 WebExceptionHandler를 Component로 등록할것 Order(-2)로 사용할것
         참고사이트 : https://stackoverflow.com/questions/49648435/http-response-exception-handling-in-spring-5-reactive
         2.
        * */
    }
}