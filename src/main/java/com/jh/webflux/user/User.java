package com.jh.webflux.user;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
public class User {
    @NotNull
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
