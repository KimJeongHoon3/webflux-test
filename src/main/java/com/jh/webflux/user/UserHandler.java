package com.jh.webflux.user;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class UserHandler {
    public Mono<ServerResponse> getUser(ServerRequest serverRequest) {
        return null;
    }

    public Mono<ServerResponse> getUser2(ServerRequest serverRequest) {
        return null;
    }

    public Mono<ServerResponse> getUser3(ServerRequest serverRequest) {
        return null;
    }

    public Mono<ServerResponse> register(ServerRequest serverRequest) {

        return null;
    }
    //등록

    //조회

    //여러개 조회

    //유효성검사

    //애러처리
}
