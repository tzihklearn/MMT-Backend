package com.sipc.mmtbackend.utils.lark;

import lombok.Data;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.05.04
 */
@Data
public class RequestData {
    private String uri;
    private String method;
    private Object parameter;
}
