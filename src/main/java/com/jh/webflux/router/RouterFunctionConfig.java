package com.jh.webflux.router;

import com.jh.webflux.user.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterFunctionConfig {
    @Bean
    public RouterFunction helloRouterFunction(){
        return RouterFunctions.route(RequestPredicates.GET("/hello")
                ,request -> ServerResponse.ok().body(Mono.just("hello"),String.class));
    }

    @Bean
    public RouterFunction userRouterFunction(){
        return nest(RequestPredicates.path("/users"),
                nest(RequestPredicates.accept(MediaType.APPLICATION_JSON)
                    ,route().GET("/{id}",handlerFunctionForGET)
                            .GET("/{id}/error",handlerFunctionForGETWithError)
                            .build())
                        .andNest(RequestPredicates.contentType(MediaType.APPLICATION_JSON),
                            route().POST("",handlerFunctionForPOST)
                                   .build())
                        .filter(this::handlingError));
//                .filter(this::handlingError));
    }

    HandlerFunction handlerFunctionForGET= request -> {
        String id=request.pathVariable("id");
        User user=new User("kim"+id,20,"basketball");
        return ServerResponse.ok().body(Mono.fromSupplier(()->user),User.class);
    };

    HandlerFunction handlerFunctionForGETWithError= request -> {
        String id=request.pathVariable("id");
        User user=new User("kim"+id,20,"basketball");
//        return Mono.fromSupplier(()->{
//            String abc=request.queryParam("abc").get();
//            return user;
//        })
//        .onErrorReturn(new User())
//        .flatMap(s-> ServerResponse.ok().bodyValue(s));
        return ServerResponse.ok().body(
                Mono.fromSupplier(()->{
                    String abc=request.queryParam("abc").get();
                    return user;
                })
                        .onErrorResume( e -> Mono.error(new RuntimeException("임의에러")))
                ,User.class);
    };

    HandlerFunction handlerFunctionForPOST= request -> {

        return ServerResponse.ok().body(Mono.just("hello"),String.class);
    };

//    HandlerFunction handlerFunctionForPOST2= request -> {
//        return ServerResponse.ok().body(Mono.just("hello"),String.class);
//    };



    private Mono<ServerResponse> handlingError(ServerRequest request, HandlerFunction<ServerResponse> next) {
        return next.handle(request)
                .onErrorResume(RuntimeException.class, e -> {
                    System.out.println("에러 찍히지: "+e.getMessage());
                    return ServerResponse.badRequest().build();
                });
    }

//    @Bean
//    public RouterFunction<ServerResponse> routes(OrderHandler orderHandler) {
//        return nest(path("/orders"),
//                nest(accept(APPLICATION_JSON),
//                        route(GET("/{id}"), orderHandler::get)
//                                .andRoute(method(HttpMethod.GET), orderHandler::list))
//                        .andNest(contentType(APPLICATION_JSON),
//                                route(POST("/"), orderHandler::create))
//                        .andNest((serverRequest) -> serverRequest.cookies().containsKey("Redirect-Traffic"),
//                                route(all(), serverRedirectHandler))
//        );
//    }
}
