package com.jh.webflux.router;

import com.jh.webflux.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.WebExceptionHandler;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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
        System.out.printf("HandlerFunction 내부 실행중..");
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

        return ServerResponse
                .ok()
                .body(request
                        .bodyToFlux(User.class)
                        .doOnNext(this::validate)
                    ,User.class);
    };

//    HandlerFunction handlerFunctionForPOST2= request -> {
//        return ServerResponse.ok().body(Mono.just("hello"),String.class);
//    };


//    <T> void validate(T t){
//        Set<ConstraintViolation<T>> validate = validator.validate(t, t.getClass());
//
//        if(!validate.isEmpty()){
//            String errorMessage=validate.stream().map(tConstraintViolation -> tConstraintViolation.getMessage())
//                    .collect(Collectors.joining("\n"));
//            throw new ClientRuntimeException(errorMessage);
//        }
//    }

    void validate(User t){

        Validator validator=createValidator();
        Set<ConstraintViolation<User>> validate = validator.validate(t, User.class);

        if(!validate.isEmpty()){
            String errorMessage=validate.stream().map(tConstraintViolation -> tConstraintViolation.getMessage())
                    .collect(Collectors.joining("\n"));
            throw new ClientRuntimeException(errorMessage);
        }
    }

    RouterFunction helloRouterFunction=nest(RequestPredicates.path("/users"),
    nest(accept(MediaType.APPLICATION_JSON)
            ,route().GET("/{id}",handlerFunctionForGET)
                    .GET("/{id}/error",handlerFunctionForGETWithError)
                    .GET("/{id}/error2",handlerFunctionForGETWithError2)
                    .after((serverRequest, serverResponse) -> {
                        System.out.println("코드 : "+serverResponse.statusCode());
                        System.out.println(Thread.currentThread().getName()+" | 동작8");
                        return serverResponse;
                    })
                    .before(serverRequest -> {
                        System.out.println(Thread.currentThread().getName()+" | 동작3");
                        return serverRequest;
                    })
                    .filter((request, next) -> {
                        System.out.println(Thread.currentThread().getName()+" | 동작4");
                        return next.handle(request)
                                .doOnNext(serverResponse -> {
                                    System.out.println(serverResponse.toString());
                                    System.out.println(serverResponse.statusCode());
                                    System.out.println(Thread.currentThread().getName()+" | 동작7");
                                });
                    }).filter((request, next) -> {
                        System.out.println(Thread.currentThread().getName()+" | 동작5");
                        return next.handle(request)
                                .doOnNext(serverResponse -> {
                                    System.out.println(serverResponse.toString());
                                    System.out.println(serverResponse.statusCode());
                                    System.out.println(Thread.currentThread().getName()+" | 동작6");
                                });
                    })
                    .build())
            .andNest(contentType(MediaType.APPLICATION_JSON),
                route().POST("",handlerFunctionForPOST)
                                   .build())
            .filter(this::handlingError)
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
                    System.out.println(Thread.currentThread().getName()+" | 동작9");
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
//                            System.out.println(Thread.currentThread().getName()+" | 동작10");
//                            ServerHttpResponse response = exchange.getResponse();
//                            response.setStatusCode(HttpStatus.BAD_GATEWAY);
//                            return response.setComplete();
//                        });
//            })
            .handlerStrategies(HandlerStrategies.builder()
                    .exceptionHandler((exchange, ex) -> {
                        if(ex instanceof ClientRuntimeException){
                            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);

                            return exchange.getResponse().setComplete();
                        }
                        else if(ex instanceof RuntimeException){
                            exchange.getResponse().setStatusCode(HttpStatus.BAD_GATEWAY);

                            return exchange.getResponse().setComplete();
                        }
                        return Mono.error(ex);
                    }).build())
            .configureClient().responseTimeout(Duration.ofHours(1))
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

//        webTestClient.get().uri("/users/2").accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody()
//                .jsonPath("$").isNotEmpty()
//                .jsonPath("$.name").isEqualTo(user.getName())
//                .jsonPath("$.age").isEqualTo(user.getAge())
//                .jsonPath("$.hobby").isEqualTo(user.getHobby())
//                .consumeWith(System.out::println);
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
           - MappingHandler로 Handler 찾고, 찾은 핸들러를 HandlerAdatper(HandlerFunctionAdpater인 경우 HandlerFunction을 실행)로 실행시키고, 그에대한 결과값인 HandlerResult를 HandlerResultHandler가 실행시켜서 결과를 셋팅하여 응답하게된다
           - filter는 HandlerAdapter가 mapping된 핸들러 실행 전후에 수행됨 (filter 또한 결과적으로 HandlerFunction으로 만들어져서 기존의 HandlerFunction에 더해지는것..)
           - 그렇다보니, 비지니스 로직을 수행을 다 진행하고 결과를 전달받는 로직으로 HandlerFunction을 만들어놓았다면, 에러 발생시 after와 같은 filter는 당연 실행안되고, 그냥 filter 에서도 response에 대한 처리 로직을 수행안됨
           - 만약, 비지니스 로직을 수행하고 값을 넣는것을 Mono로 감싸서 response의 body로 넘겨주어 파이프라인을 만들었다면, body 내부에 있는 Mono는 HandlerResultHandler에서 결과값을 셋팅할때 수행될테니, 만약 에러가 난다해도 filter에서 절대 잡힐수가없다..
           - 그러므로 webFilter나, WebExceptionHandler 에서 예외를 처리해주어야한다!
            - *filter는 handler를 수행하기전에 필요한 로직을 메서드안에 정의할수있고, response에 대한것은 next.handler(request)를 통해(이게 Mono에 감싸진 response 리턴해줌)에 정의할수있다..

         2. Validation

        * */
    }

    @Test
    void POST_정상(){
        User user=new User("kim",50,"basketball");
        webTestClient.post().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(user)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isNotEmpty()
                .jsonPath("$").isArray()
                .jsonPath("$[0].name").isEqualTo(user.getName())
                .jsonPath("$[0].age").isEqualTo(user.getAge())
                .jsonPath("$[0].hobby").isEqualTo(user.getHobby())
                .consumeWith(System.out::println);
    }

    @Test
    void POST_에러_validation(){
        User user=new User();
        user.setAge(50);
        user.setHobby("validationError");
        webTestClient.post().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(user)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
//                .jsonPath("$").isNotEmpty()
//                .jsonPath("$").isArray()
//                .jsonPath("$[0].name").isEqualTo(user.getName())
//                .jsonPath("$[0].age").isEqualTo(user.getAge())
//                .jsonPath("$[0].hobby").isEqualTo(user.getHobby())
                .consumeWith(System.out::println);
    }

    private Validator createValidator() {
//        return Validation.buildDefaultValidatorFactory().getValidator();
        LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean(); //spring에서 사용하도록 만든것..

        localValidatorFactoryBean.afterPropertiesSet(); //초기화하는거.. 반드시필요
        return localValidatorFactoryBean;
    }

    static class ClientRuntimeException extends RuntimeException{
        public ClientRuntimeException(String message){
            super(message);
        }
    }
}