package com.sipc.mmtbackend.mapper.c;

import com.sipc.mmtbackend.pojo.c.domain.LoginStatus;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author valentine
 */
@Mapper
public interface LoginStatusMapper {
    /**
     * 删除当前已存在的登录状态，保证用户登录唯一
     * @param record
     * @return
     */
    int deleteByValue(String record);

    int deleteByKey(String record);

    /**
     * 查询用户是否在其他地方登录，验证登录是否唯一
     * @param record
     * @return
     */
    LoginStatus selectByValue(String record);

    /**
     * 根据loginStatus插入数据,维护新的登录状态
     * @param record
     * @return
     */
    int insert(LoginStatus record);

    int updateSelective(LoginStatus record);

    int insertWithOrgId(LoginStatus record);
}
