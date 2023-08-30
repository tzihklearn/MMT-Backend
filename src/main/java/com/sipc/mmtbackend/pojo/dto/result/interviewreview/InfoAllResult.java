package com.sipc.mmtbackend.pojo.dto.result.interviewreview;

import com.sipc.mmtbackend.pojo.dto.result.interviewreview.po.*;
import lombok.Data;

import java.util.List;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.08.28
 */
@Data
public class InfoAllResult {

    private List<QuestionPo> title;

    private List<InfoPo> tableData;

    private Integer page;

    private Integer pageAll;

    private Integer allNum;

    private Integer round;

}
