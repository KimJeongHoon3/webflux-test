package com.jh.webflux.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Level {
    GOLD(3,null),
    SILVER(2,GOLD),
    BASIC(1,SILVER);

    private final int value;
    private final Level nextLevel;

    Level(int value,Level nextLevel){
        this.value = value;
        this.nextLevel=nextLevel;
    }

    @JsonValue
    public int intValue() {
        return value;
    }

    public Level nextLevel(){
        return nextLevel;
    }

    public static Level valueOf(int value){
        switch (value){
            case 1 : return BASIC;
            case 2 : return SILVER;
            case 3 : return GOLD;
            default : throw new UnsupportedOperationException("not defined type : "+value);
        }
    }

    @JsonCreator
    public static Level fromJson(int value){
        return Level.valueOf(value);
    }
}
