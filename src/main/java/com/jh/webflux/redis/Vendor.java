package com.jh.webflux.redis;

public enum Vendor {

    INFO("30000"), UPLUS("30001");
    String value;

    Vendor(String value){
        this.value=value;
    }

    String getValue(){
        return this.value;
    }
}
