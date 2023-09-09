package com.sipc.mmtbackend.pojo.c.result;

import lombok.Data;

@Data
public class IdNameResult {
    Integer id;
    String name;

    public IdNameResult(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
