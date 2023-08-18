package com.sipc.mmtbackend.pojo.dto.param.superAdmin;

import com.sipc.mmtbackend.pojo.dto.data.InterviewFromData;
import lombok.Data;

import java.util.List;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.08.17
 */
@Data
public class InterviewFormParam {

    private Integer allRound;

    private List<InterviewFromData> list;

}
