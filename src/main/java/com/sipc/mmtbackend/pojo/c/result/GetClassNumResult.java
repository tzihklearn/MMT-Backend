package com.sipc.mmtbackend.pojo.c.result;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GetClassNumResult {
    private List<IdNameResult> classNum;

    public GetClassNumResult(){
        List<IdNameResult> cls = new ArrayList<>();
        this.setClassNum(cls);
    }
}
