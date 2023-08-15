package com.sipc.mmtbackend.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sipc.mmtbackend.pojo.dto.data.QuestionValueListData;
import com.sipc.mmtbackend.pojo.dto.data.QuestionValueData;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.06.03
 */
public class TransQuestionJsonTest {

//    @Test
    public void test() {

        QuestionValueListData questionValueListData = new QuestionValueListData();

        List<QuestionValueData> questionValueDataList = new ArrayList<>();

        QuestionValueData questionValueData1 = new QuestionValueData();
        questionValueData1.setValue("请输入你的班级");
        questionValueData1.setChildValueList(null);

        questionValueDataList.add(questionValueData1);

//        QuestionValueData questionValueData2 = new QuestionValueData();
//        questionValueData2.setValue("女");
//        questionValueData2.setChildValueList(null);
//
//        questionValueDataList.add(questionValueData2);
        questionValueListData.setQuestionValueDataList(questionValueDataList);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String s = objectMapper.writeValueAsString(questionValueListData);

            QuestionValueListData questionValueListData1 = objectMapper.readValue(s, QuestionValueListData.class);
            System.out.println(questionValueListData1);
//            System.out.println(s);
            System.out.println(s);
            System.out.println(s.replaceAll("\"", "\\\\\""));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
