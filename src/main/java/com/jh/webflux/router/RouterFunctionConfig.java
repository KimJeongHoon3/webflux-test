package com.jh.webflux.router;

import com.jh.webflux.user.UserHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.*;

@Configuration
@Slf4j
public class RouterFunctionConfig {
    @Bean
    public RouterFunction helloRouterFunction(){
        return RouterFunctions.route(RequestPredicates.GET("/hello")
                ,request -> ServerResponse.ok().body(Mono.just("hello"),String.class));
    }

    @Bean
    public RouterFunction userRouterFunction(UserHandler user){
        return nest(path("/users")
                ,nest(accept(MediaType.APPLICATION_JSON)
                        ,route().GET("{id}",user::getUser)
                                .GET("/{id}/error",user::error)
                                .GET("/{id}/error2",user::error2)
                                .build()
                ).andNest(contentType(MediaType.APPLICATION_JSON)
                        ,route().POST(user::register)
                                .POST("/error",user::postError)
                                .build()
                ).filter((request, next) -> next
                        .handle(request)
                        .doOnNext(serverResponse -> {
                            log.info("서버 응답: "+serverResponse.statusCode());
                        }).onErrorResume(throwable -> {
                            log.info("에러 : "+throwable.getMessage());
                            return ServerResponse.badRequest().bodyValue("{\"message\":\"error\"}");
                        }))
        );
    }

}
