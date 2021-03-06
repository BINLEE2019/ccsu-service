package com.hang.service;

import com.hang.bbs.common.Page;
import com.hang.dao.NotificationDAO;
import com.hang.pojo.data.NotificationDO;
import com.hang.enums.NotificationEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zhanghang
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class NotificationService {

    @Autowired
    private NotificationDAO notificationDAO;

    /**
     * 保存通知
     *
     * @param notificationDO
     */
    public void save(NotificationDO notificationDO) {
        notificationDAO.insertSelective(notificationDO);
    }

    /**
     * 发送通知
     *
     * @param action
     * @param notificationId
     * @param content
     */
    public void sendNotification(String openId, String targetOpenId, NotificationEnum action, Integer notificationId, String content) {
        NotificationDO notificationDO = new NotificationDO();
        notificationDO.setOpenId(openId);
        notificationDO.setTargetOpenId(targetOpenId);
        notificationDO.setInTime(new Date());
        notificationDO.setNotificationId(notificationId);
        notificationDO.setAction(action.name());
        notificationDO.setContent(content);
        notificationDO.setIsRead(false);
        save(notificationDO);
    }

    /**
     * 根据用户查询评论等通知
     *
     * @return
     */
    public Page<Map> findCommentNoteByOpenId(Integer pageNo, Integer pageSize, String targetOpenId) {
        List<Map> list = notificationDAO.findCommentNoteByOpenId(targetOpenId, (pageNo - 1) * pageSize, pageSize,
                "n.is_read asc, n.id desc");
        int count = notificationDAO.countByTargetOpenId(targetOpenId);
        return new Page<>(pageNo, pageSize, count, list);
    }

    /**
     * 根据用户查询系统通知
     */
    public Page<Map> findSystemNoteByOpenId(Integer pageNo, Integer pageSize, String targetOpenId) {
        List<Map> list = notificationDAO.findSystemNoteByOpenId(targetOpenId, (pageNo - 1) * pageSize, pageSize,
                "n.is_read asc, n.id desc");
        int count = notificationDAO.countByTargetOpenId(targetOpenId);
        return new Page<>(pageNo, pageSize, count, list);
    }



    /**
     * 根据用户查询通知
     *
     * @return
     */
    public long countByTargetUserAndIsRead(String targetOpenId) {
        return notificationDAO.countByTargetOpenId(targetOpenId);
    }

    /**
     * 批量更新通知的状态
     */
    public void updateByIsRead(String targetOpenId) {
        notificationDAO.updateByIsRead(targetOpenId);
    }

    /**
     * 删除目标用户的通知
     */
    public void deleteByTargetUser(String targetOpenId) {
        notificationDAO.deleteNotification(targetOpenId, null, null);
    }

    /**
     * 话题被删除了，删除由话题引起的通知信息
     *
     * @param topicId
     */
    public void deleteByTopic(Integer topicId) {
        notificationDAO.deleteNotification(null, null, topicId);
    }

}
