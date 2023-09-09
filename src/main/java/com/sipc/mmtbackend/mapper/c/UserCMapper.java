package com.sipc.mmtbackend.mapper.c;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sipc.mmtbackend.pojo.c.domain.UserC;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface UserCMapper extends BaseMapper<UserC> {
    /**
     * 插入user
     *
     * @param record
     * @return
     */
    int insertSelective(UserC record);

    /**
     * 根据openid更新表
     *
     * @param user
     * @return
     */
    int updateSelective(UserC user);

    /**
     * 根据studentId更新表
     *
     * @param user
     * @return
     */
    int updateByStudentIdSelective(UserC user);

    /**
     * 根据主键更新表
     *
     * @param user
     * @return
     */
    int updateByPrimaryKey(UserC user);

    /**
     * 通过openid得到User
     *
     * @param openid
     * @return
     */
    UserC getUserByOpenid(String openid);

    /**
     * 检验账号密码是否正确，返回user
     *
     * @param record
     * @return
     */
//    User selectByStudentIdAndPassword(LoginParam record);

    /**
     * 检验studentId是否存在，返回user
     *
     * @param record
     * @return
     */
    UserC selectByStudentId(Integer record);

    /**
     * 根据userId查找user
     *
     * @param record
     * @return
     */

    UserC selectByPrimaryKey(Integer record);

    /*
     * 根据主键删除记录
     */
    int deleteByPrimaryKey(Integer Id);

    /**
     * 通过openId 得到 userId
     */
    Integer selectIdByOpenId(String openId);

    /**
     * 通过studentId 得到 userId
     *
     * @return
     */
    Integer selectIdByStudentId(String studentId);

    UserC selectByOpenid(String openid);

    int updateOpenidByPrimaryKey(@Param("openid") String openid, @Param("key") Integer key);

    String selectNameById(Integer id);

    UserC selectById(Integer id);

    Boolean insertAll(Integer studentId, String name, String phone, String openid);

    UserC selectAllByStudentIdUser(Integer studentId);

}

