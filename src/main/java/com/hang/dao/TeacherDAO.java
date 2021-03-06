package com.hang.dao;

import com.hang.pojo.data.TeacherDO;
import org.springframework.stereotype.Repository;

/**
 * @author LEO-BIN
 * @date 2019/7/15
 */
@Repository
public interface TeacherDAO {

    /**
     * 授权给学生
     * @param jwcAccount
     */
    int authorizeToStudent(String jwcAccount);

    TeacherDO getTeacherByOpenId(String openId);

    int insert(TeacherDO teacherDOO);

    int update(TeacherDO teacherDO);
}
