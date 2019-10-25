package com.hang.service;

import com.hang.annotation.OpenId;
import com.hang.dao.NotificationDAO;
import com.hang.dao.TeacherDAO;
import com.hang.dao.UserInfoDAO;
import com.hang.enums.NotificationEnum;
import com.hang.exceptions.ApiAssert;
import com.hang.exceptions.ApiException;
import com.hang.manage.UserCache;
import com.hang.pojo.data.StudentDO;
import com.hang.pojo.data.SystemNotificationDO;
import com.hang.pojo.data.TeacherDO;
import com.hang.pojo.data.UserInfoDO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.hang.constant.InformationConstant.AUTHORIZE_SUCCESS;

/**
 * @author leo-bin
 * @date 2019/7/16
 */
@Service
public class TeacherService {

    @Autowired
    private TeacherDAO teacherDAO;

    @Autowired
    private UserInfoDAO userInfoDAO;

    @Autowired
    private UserService userService;

    @Autowired
    private UserCache userCache;

    @Autowired
    private NotificationDAO notificationDAO;

    @Autowired
    private  NotificationService notificationService;


    /**
     * 教师绑定教工号
     *
     * @apiNote 在官网做模拟登陆，进行信息的校验
     */
    @Transactional(rollbackFor = Exception.class)
    public void bindForTeacher(String openId, String account, String code) {
        String name = account;
        userService.updateJwcAccount(openId, code);
        UserInfoDO userInfoDO = userInfoDAO.selectByOpenId(openId);
        TeacherDO teacherDO = new TeacherDO();
        teacherDO.setStaffNum(userInfoDO.getJwcAccount());
        teacherDO.setCode(code);
        teacherDO.setNickName(userInfoDO.getNickName());
        teacherDO.setOpenId(userInfoDO.getOpenId());
        teacherDO.setName(name);
        TeacherDO teacherInfo = getTeacherInfo(openId);
        if (Objects.isNull(teacherInfo)) {
            saveTeacherInfo(teacherDO);
        } else {
            modifyTeacherInfo(teacherDO);
        }
        userService.updateUserRole(openId, 1);
        UserInfoDO userInfo = userInfoDAO.selectByOpenId(openId);
        //redis缓存穿透
        userCache.updateUserInfo(openId, userInfo);
    }


    /**
     * 获取老师信息
     * 为了防止信息没有给全，没有输入教工号
     */
    public TeacherDO getTeacherInfo(String openId) {
        if (StringUtils.isBlank(openId)) {
            throw new ApiException(-1, "openId为空");
        }
        TeacherDO teacherDO = teacherDAO.getTeacherByOpenId(openId);
        if (teacherDO != null) {
            if (StringUtils.isBlank(teacherDO.getStaffNum())) {
                teacherDO.setStaffNum("未绑定");
            }
        }
        return teacherDO;
    }

    /**
     * 保存教师信息
     */
    public void saveTeacherInfo(TeacherDO teacherDO) {
        int i = teacherDAO.insert(teacherDO);
        ApiAssert.nonEqualInteger(i, 1, "保存失败");
    }

    /**
     * 修改教师信息
     */
    public void modifyTeacherInfo(TeacherDO teacherDO) {
        int i = teacherDAO.update(teacherDO);
        ApiAssert.nonEqualInteger(i, 1, "更新失败");
    }

    /**
     * 教师给学生授权
     */
    public void authorizeToStudent(@OpenId String openId, String jwcAccount) {
        int i = teacherDAO.authorizeToStudent(jwcAccount);
        ApiAssert.nonEqualInteger(i, 1, "授权失败");
        //授权成功，给被授权人发通知
        UserInfoDO userInfoDO=userService.getUserInfoByJwcAccount(jwcAccount);
        SystemNotificationDO systemNotificationDO=new SystemNotificationDO();
        systemNotificationDO.setNoteType(NotificationEnum.SYSTEM_NOTE_PUBLIC.name());
        systemNotificationDO.setMessage(AUTHORIZE_SUCCESS);
        notificationDAO.insertSystemNote(systemNotificationDO);
        Integer notificationId=systemNotificationDO.getId();
        notificationService.sendNotification(openId,userInfoDO.getOpenId(),NotificationEnum.SYSTEM_NOTE_PUBLIC,notificationId,AUTHORIZE_SUCCESS,"");
    }
}
