package com.hang.dao;

import com.hang.CcsuServiceApplicationTests;
import com.hang.enums.LostPropertyAndRecruitEnum;
import com.hang.pojo.data.LostPropertyAndRecruitDO;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author test
 * @date 19-4-28
 * *****************
 * function:
 */
public class LostPropertyAndRecruitDAOTest extends CcsuServiceApplicationTests {

    @Autowired
    private LostPropertyAndRecruitDAO lostPropertyAndRecruitDAO;

    @Test
    public void insert() throws Exception {
        LostPropertyAndRecruitDO lostPropertyAndRecruitDO = new LostPropertyAndRecruitDO();
        lostPropertyAndRecruitDO.setInitiatorName("张航");
        lostPropertyAndRecruitDO.setCategory("LostProperty");
        lostPropertyAndRecruitDO.setInitiatorJwcAccount("B20150304203");
        lostPropertyAndRecruitDO.setContactInformation("18374976843");
        lostPropertyAndRecruitDO.setInitiatorMessage("this is test message 3");
        lostPropertyAndRecruitDO.setInitiatorLocation("含蓄楼");
        lostPropertyAndRecruitDO.setOccurTime(new Date());
        lostPropertyAndRecruitDO.setDatetime(new Date());
        lostPropertyAndRecruitDAO.insert(lostPropertyAndRecruitDO);
    }

    @Test
    public void list() throws Exception {
        List<LostPropertyAndRecruitDO> lostPropertyAndRecruitDOS = lostPropertyAndRecruitDAO.listAll(0, 10);
        lostPropertyAndRecruitDOS.forEach(System.out::println);
    }

    @Test
    public void listByCategory() throws Exception {
        List<LostPropertyAndRecruitDO> lostPropertyAndRecruitDOS = lostPropertyAndRecruitDAO.listByCategory(LostPropertyAndRecruitEnum.LOSTPROPERTY.name(), 0, 10);
        lostPropertyAndRecruitDOS.forEach(System.out::println);
        System.out.println("********");
        List<LostPropertyAndRecruitDO> lostPropertyAndRecruitDOS2 = lostPropertyAndRecruitDAO.listByCategory(LostPropertyAndRecruitEnum.RECRUIT.name(), 0, 10);
        lostPropertyAndRecruitDOS2.forEach(System.out::println);
    }

}