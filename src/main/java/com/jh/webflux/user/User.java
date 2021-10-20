package com.jh.webflux.user;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    public User(@NotNull(message = "name must have value") String name, int age, String hobby) {
        this.name = name;
        this.age = age;
        this.hobby = hobby;
    }

    @NotNull(message = "name must have value")
    private String name;
    private int age;
    private String hobby;

    @NotNull(message = "level must have value")
    private Level level;
    private int recommendCnt;
    private int loginCnt;

    public void upgradeLevel() {
        Level nextLevel=level.nextLevel();
        if(nextLevel==null){
            throw new IllegalStateException("cannot upgrade next Level");
        }else{
            level=nextLevel;
        }
    }
}
