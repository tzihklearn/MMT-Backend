package com.sipc.mmtbackend.generator;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.TemplateType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.apache.ibatis.annotations.Mapper;
import org.junit.jupiter.api.Test;

import java.util.Collections;


/**
 * @author tzih
 * @since 2023.03.20
 */

public class CodeGeneratorTest {


    @Test
    public void Test() {

        String url = "jdbc:mysql://ip/mmtbackend?useUnicode=true&characterEncoding=UTF-8&serverTimezone=GMT%2B8";
        String username = "name";
        String password = "passwd";

        FastAutoGenerator
                //连接数据库
                .create(url, username, password)
                //全局配置
                .globalConfig(builder -> builder.author("tzih")
                        .outputDir("src/main/java"))
                //包配置
                .packageConfig(builder -> builder.parent("com.sipc")
                        .moduleName("mmtbackend")
                        .entity("pojo.domain")
                        .mapper("mapper")
                        .xml("mapper.xml")
                        .pathInfo(Collections.singletonMap(OutputFile.xml, "src/main/resources/mapper")))
                //策略配置,要生成的表的表名
                .strategyConfig(builder -> builder.addInclude("organization", "organization_tag_merge",
                                "organization_recruit", "organization_department_merge", "department", "tag")
                        //实体类策略配置，enableFileOverride覆盖原文件
//                        .entityBuilder().enableLombok().enableTableFieldAnnotation().idType(IdType.AUTO).logicDeleteColumnName("is_deleted").enableFileOverride()
                        .entityBuilder().enableLombok().enableTableFieldAnnotation().idType(IdType.AUTO).enableFileOverride()
                        //mapper类策略配置
                        .mapperBuilder().mapperAnnotation(Mapper.class).enableFileOverride())
                //模板配置
                .templateEngine(new FreemarkerTemplateEngine())
                .templateConfig(builder -> {
                    //禁用相关的模板
                    builder.disable(TemplateType.CONTROLLER)
                            .disable(TemplateType.SERVICE)
                            .disable(TemplateType.SERVICE_IMPL);
                })
                .execute();
    }

}
