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

//        QuestionValueListData questionValueListData = new QuestionValueListData();

        List<String> questionValueDataList = new ArrayList<>();

        questionValueDataList.add("请输入你的姓名");
        questionValueDataList.add("qing");

//        QuestionValueData questionValueData1 = new QuestionValueData();
//        questionValueData1.setValue("请输入你的姓名");
//        questionValueData1.setChildValueList(null);
//
//        questionValueDataList.add(questionValueData1);

//        QuestionValueData questionValueData2 = new QuestionValueData();
//        questionValueData2.setValue("女");
//        questionValueData2.setChildValueList(null);
//
//        questionValueDataList.add(questionValueData2);
//        questionValueListData.setQuestionValueDataList(questionValueDataList);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
//            String s = objectMapper.writeValueAsString(questionValueListData);
            String s = objectMapper.writeValueAsString(questionValueDataList);

            List<String> questionValueListData1 = objectMapper.readValue(s, List.class);
            System.out.println(questionValueListData1);
//            System.out.println(s);
            System.out.println(s);
//            System.out.println(s.replaceAll("\"", "\\\\\""));
            List<String> t = objectMapper.readValue("[\"请输入你的姓名\"]", List.class);
            System.out.println(t);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
