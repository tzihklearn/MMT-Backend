package com.sipc.mmtbackend.pojo.domain.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.08.15
 */
@Data
public class GroupLocalTimeCountPo {
    private LocalDateTime id;

    private Integer count;
}
