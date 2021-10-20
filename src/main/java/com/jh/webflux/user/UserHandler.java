package com.jh.webflux.user;

import com.jh.webflux.exception.CustomResponseException;
import com.jh.webflux.validator.CustomValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.xml.validation.Validator;

@Component
@Slf4j
public class UserHandler {
    public Mono<ServerResponse> getUser(ServerRequest serverRequest) {
        log.info("id : "+serverRequest.pathVariable("id"));
        User user=new User("kim",50,"hobby");
        return ServerResponse.ok().bodyValue(user);
    }

    public Mono<ServerResponse> error(ServerRequest serverRequest) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(()->{
                    String abc=serverRequest.queryParam("abc").get(); //에러발생..
                    User user=new User("kim2",50,"hobby2");
                    return user;
                })
                        .onErrorResume( e -> Mono.error(new CustomResponseException(HttpStatus.BAD_GATEWAY,"임의에러")))
                ,User.class);
    }

    public Mono<ServerResponse> error2(ServerRequest serverRequest) {
        String id=serverRequest.pathVariable("id");
        User user=new User("kim"+id,20,"basketball");
        if(1==1) throw new RuntimeException("일부러");
        return ServerResponse.ok().bodyValue(user);
    }

    public Mono<ServerResponse> register(ServerRequest serverRequest) {

        return ServerResponse.ok()
                .body(serverRequest.bodyToMono(User.class)
                                .doOnNext(CustomValidator::validate)
                    ,User.class);
    }

    public Mono<ServerResponse> postError(ServerRequest serverRequest) {
        return null;
    }
    //등록

    //조회

    //여러개 조회

    //유효성검사

    //애러처리
}
