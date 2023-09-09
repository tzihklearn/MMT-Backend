package com.sipc.mmtbackend.pojo.c.result.SuggestionResult;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class UploadPhotoResult {
    private Boolean success;
    private String code;
    private String message;
    private Map<String, String> data = new HashMap<>();
    private String RequestId;
}
