package com.sipc.mmtbackend.utils.CheckroleBUtil;

import com.sipc.mmtbackend.utils.CheckroleBUtil.pojo.PermissionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 鉴权相关工具
 *
 * @author DoudiNCer
 */
@Component
@Slf4j
public class CheckRoleUtil {
//    private final Map<String, PermissionEnum> apiPermissions;

//    @Autowired
//    public CheckRoleUtil() {
//        Map<String, PermissionEnum> apiPermissions = new HashMap<>();
//        apiPermissions.put("/b/admin/organization/info/update", PermissionEnum.SUPER_ADMIN);
//        apiPermissions.put("/b/admin/organization/info/get", PermissionEnum.SUPER_ADMIN);
//        apiPermissions.put("/b/admin/organization/avatar/upload", PermissionEnum.SUPER_ADMIN);
//
//        apiPermissions.put("/b/admin/organization/admission/publish", PermissionEnum.SUPER_ADMIN);
//        apiPermissions.put("/b/admin/organization/registration/form/save", PermissionEnum.SUPER_ADMIN);
//        apiPermissions.put("/b/admin/organization/registration/form/info", PermissionEnum.SUPER_ADMIN);
//        apiPermissions.put("/b/admin/organization/registration/form/system/question", PermissionEnum.SUPER_ADMIN);
//        apiPermissions.put("/b/admin/organization/registration/form/select/type", PermissionEnum.SUPER_ADMIN);
//
//        apiPermissions.put("/b/admin/account/icode/generated", PermissionEnum.SUPER_ADMIN);
//        apiPermissions.put("/b/admin/account/members/info", PermissionEnum.SUPER_ADMIN);
//        apiPermissions.put("/b/admin/account/members/info/sift", PermissionEnum.SUPER_ADMIN);
//        apiPermissions.put("/b/admin/account/members/revise/info", PermissionEnum.SUPER_ADMIN);
//        apiPermissions.put("/b/admin/account/members/revise/permission", PermissionEnum.SUPER_ADMIN);
//        apiPermissions.put("/b/admin/account/members/delete", PermissionEnum.SUPER_ADMIN);
//        this.apiPermissions = apiPermissions;
//    }

    /**
     * 检验B端用户权限
     *
     * @param permissionId 待验证权限id
     * @param url          请求的url
     * @return 返回是否具有访问权限
     */
//    public boolean bCheck(Integer permissionId, String url) {
//        PermissionEnum permissionEnum = apiPermissions.get(url);
//
//        if (permissionEnum == null) {
//            return false;
//        }
//
//        return (permissionId <= permissionEnum.getId());
//    }
}
