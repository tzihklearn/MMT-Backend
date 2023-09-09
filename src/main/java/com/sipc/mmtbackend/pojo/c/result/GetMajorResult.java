package com.sipc.mmtbackend.pojo.c.result;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GetMajorResult {
    private List<IdNameResult> major;

    public GetMajorResult() {
        List<IdNameResult> major = new ArrayList<>();
        this.setMajor(major);
    }
}
