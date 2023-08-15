package com.sipc.mmtbackend.generator;

import com.sipc.mmtbackend.utils.CheckroleBUtil.PasswordUtil;
import com.sipc.mmtbackend.utils.ICodeUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.05.29
 */
@SpringBootTest
public class BPasswdTest {


//    private final OrganizationDepartmentMergeMapper organizationDepartmentMergeMapper;
//
//    @Autowired
//    public BPasswdTest(OrganizationDepartmentMergeMapper organizationDepartmentMergeMapper) {
//        this.organizationDepartmentMergeMapper = organizationDepartmentMergeMapper;
//    }
//
    private final ICodeUtil iCodeUtil;

    @Autowired
    public BPasswdTest(ICodeUtil iCodeUtil) {
        this.iCodeUtil = iCodeUtil;
    }

//    @Test
    public void test() {
        System.out.println(PasswordUtil.hashPassword("123456"));

        for (int i = 7; i <= 32; ++i) {
            System.out.println("社团"+i+": " + iCodeUtil.setICodeRedis(i, 6));
        }

//        for (Map<String, Object> selectMap : organizationDepartmentMergeMapper.selectMaps(
//                new QueryWrapper<OrganizationDepartmentMerge>()
//                        .eq("organization_id", 2)
//                        .select("organization_id")
//                        .select("department_id")
//                        .select("is_deleted")
//        )) {
//            System.out.println(selectMap);
//        }

    }
}
