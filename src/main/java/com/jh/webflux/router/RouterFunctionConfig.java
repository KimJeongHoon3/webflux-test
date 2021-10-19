package com.jh.webflux.router;

import com.jh.webflux.user.User;
import com.jh.webflux.user.UserHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.*;

@Configuration
public class RouterFunctionConfig {
    @Bean
    public RouterFunction helloRouterFunction(){
        return RouterFunctions.route(RequestPredicates.GET("/hello")
                ,request -> ServerResponse.ok().body(Mono.just("hello"),String.class));
    }

    @Bean
    public RouterFunction userRouterFunction(@Autowired UserHandler user){
        return nest(path("/users")
                ,nest(accept(MediaType.APPLICATION_JSON)
                        ,route().GET(user::getUser)
                                .GET("/error",user::getUser2)
                                .GET("/error2",user::getUser3)
                                .build()
                ).andNest(contentType(MediaType.APPLICATION_JSON)
                        ,route().POST(user::register)
                                .build()
                )
        );
    }

}
