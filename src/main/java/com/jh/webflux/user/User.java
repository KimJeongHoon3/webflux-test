package com.jh.webflux.user;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
@ToString
public class User {
    @NotNull(message = "name must have value")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String name;
    private int age;
    private String hobby;

//    @NotNull
//    private Level level;
//    private int recommendCnt;
//    private int loginCnt;
//
//    public void upgradeLevel() {
//        Level nextLevel=level.nextLevel();
//        if(nextLevel==null){
//            throw new IllegalStateException("cannot upgrade next Level");
//        }else{
//            level=nextLevel;
//        }
//    }
}
