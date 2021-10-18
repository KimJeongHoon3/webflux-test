package com.jh.webflux.validator;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class Book {
    @NotNull(message = "id not null")
    String id;

    @NotBlank(message = "name is not blank")
    String name;

    @NotEmpty(message = "subName is not empty")
    String subName;

    public Book() {
    }

    public Book(@NotNull(message = "id not null") String id, @NotBlank String name, @NotEmpty String subName) {
        this.id = id;
        this.name = name;
        this.subName = subName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }
}
