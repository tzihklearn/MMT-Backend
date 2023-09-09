package com.sipc.mmtbackend.pojo.c.result;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GetAcademyResult {
    private List<IdNameResult> academy;

    public GetAcademyResult(){
        List<IdNameResult> aca = new ArrayList<>();
        this.setAcademy(aca);
    }
}
