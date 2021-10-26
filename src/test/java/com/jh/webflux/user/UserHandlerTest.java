package com.jh.webflux.user;

import com.jh.webflux.config.Config;
import com.jh.webflux.exception.CustomWebExceptionHandler;
import com.jh.webflux.router.RouterFunctionConfig;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.JsonPathAssertions;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.blockhound.BlockHound;

import java.io.FileInputStream;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureWebTestClient
@WebFluxTest(properties = "server.error.include-message=always")
@Import({RouterFunctionConfig.class,UserHandler.class, CustomWebExceptionHandler.class, Config.class})
class UserHandlerTest {
    @Autowired
    WebTestClient webTestClient;

    @BeforeEach
    public void setUp() {
        webTestClient = webTestClient
                .mutate()
                .responseTimeout(Duration.ofHours(1))
                .build();
    }

    @Test
    void GET_정상(){
        WebTestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = webTestClient.get();
        WebTestClient.RequestHeadersSpec<?> uri = requestHeadersUriSpec.uri("/users/1");
        WebTestClient.ResponseSpec exchange = uri.exchange();
        WebTestClient.BodyContentSpec bodyContentSpec = exchange.expectBody();
        JsonPathAssertions jsonPathAssertions = bodyContentSpec.jsonPath("$");
        WebTestClient.BodyContentSpec notEmpty = jsonPathAssertions.isNotEmpty();
        JsonPathAssertions jsonPathAssertions2 = notEmpty.jsonPath("$.name");
        WebTestClient.BodyContentSpec kim = jsonPathAssertions2.isEqualTo("kim");
        kim.consumeWith(System.out::println);
    }

    @Test
    void GET_에러발생(){
        webTestClient.get().uri("/users/1/error")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.message").isEqualTo("임의에러")
                .consumeWith(System.out::println);
    }

    @Test
    void GET_에러발생2(){
        webTestClient.get().uri("/users/1/error2")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.message").isEqualTo("일부러")
                .consumeWith(System.out::println);
    }

    @Test
    void POST_정상(){
        BlockHound.install();
        User user=new User("kim",50,"hobby",Level.BASIC,1,1);
        webTestClient.post().uri("/users").contentType(MediaType.APPLICATION_JSON)
                .bodyValue(user)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo(user.getName())
                .jsonPath("$.age").isEqualTo(user.getAge())
                .jsonPath("$.hobby").isEqualTo(user.getHobby())
                .jsonPath("$.level").isEqualTo(Level.BASIC.intValue())
                .jsonPath("$.recommendCnt").isEqualTo(user.getRecommendCnt())
                .jsonPath("$.loginCnt").isEqualTo(user.getLoginCnt())
                .consumeWith(System.out::println);
    }

    @Test
    void POST_에러_validation에러(){
        User user=new User();
        user.setAge(10);
        user.setHobby("hobby");
        webTestClient.post().uri("/users").contentType(MediaType.APPLICATION_JSON)
                .bodyValue(user)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.message").value(StringContains.containsString("level must have value"))
                .jsonPath("$.message").value(StringContains.containsString("name must have value"))
                .consumeWith(System.out::println);
    }
}