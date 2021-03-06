package com.hang.api;

import com.hang.aop.StatisticsTime;
import com.hang.enums.ResultEnum;
import com.hang.pojo.data.AdviserDO;
import com.hang.pojo.vo.BaseRes;
import com.hang.service.AdviserService;
import com.hang.service.InformationService;
import com.hang.utils.RespUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;


/**
 * @author  LEO-BIN
 * @date 20197/15
 * 创新创业控制器层
 */
@Api("双创相关接口")
@RestController
@RequestMapping("/creative")
public class CreativeController {

    @Autowired
    private AdviserService adviserService;

    @Autowired
    private InformationService informationService;

    @StatisticsTime("getAdvisers")
    @ApiOperation("查看所有导师的信息")
    @GetMapping("/getAdvisers")
    public BaseRes getAdvisers(@RequestParam(required = false, defaultValue = "0") int start,
                               @RequestParam(required = false, defaultValue = "100") int offset) {
        List<AdviserDO> adviserDOS = adviserService.getAdvisers(start, offset);
        return RespUtil.success(adviserDOS);
    }

    /**
     * 查询导师信息
     *
     * @param id
     * @return
     */
    @StatisticsTime("getAdviser")
    @ApiOperation("根据id获取单个导师信息")
    @GetMapping("/getAdviser")
    public BaseRes getInformationById(@RequestParam int id) {
        return RespUtil.success(adviserService.getAdviser(id));
    }

    /**
     * 增加导师信息
     */
    @StatisticsTime("insertAdviserInfo")
    @ApiOperation("增加导师信息")
    @PostMapping("/insertAdviserInfo")
    public BaseRes insertAdviserInfo(@RequestParam String adviserName,
                                     @RequestParam String adviserTel,
                                     @RequestParam String adviserInfo,
                                     @RequestParam String department,
                                     @RequestParam String avatar,
                                     @RequestParam String email,
                                     @RequestParam String office,
                                     @RequestParam String education,
                                     @RequestParam String position,
                                     @RequestParam String teachingCourse,
                                     @RequestParam String researchDirection
    ) {
        if (adviserName == null) {
            return RespUtil.error(ResultEnum.ADVISERNAME_IS_NULL);
        }
        AdviserDO adviserDo = new AdviserDO();
        adviserDo.setName(adviserName);
        adviserDo.setTel(adviserTel);
        adviserDo.setInfo(adviserInfo);
        adviserDo.setDepartment(department);
        adviserDo.setAvatar(avatar);
        adviserDo.setEmail(email);
        adviserDo.setOffice(office);
        adviserDo.setEducation(education);
        adviserDo.setPosition(position);
        adviserDo.setTeachingCourse(teachingCourse);
        adviserDo.setResearchDirection(researchDirection);
        adviserService.insertAdviserInfo(adviserDo);
        return RespUtil.success();
    }

    /**
     * 修改导师信息
     */
    @StatisticsTime("updateAdviserInfo")
    @ApiOperation("修改导师信息")
    @PostMapping("/updateAdviserInfo")
    public BaseRes updateAdviserInfo(
            @RequestParam Integer id,
            @RequestParam String adviserName,
            @RequestParam String adviserTel,
            @RequestParam String adviserInfo,
            @RequestParam String department,
            @RequestParam String avatar,
            @RequestParam String email,
            @RequestParam String office,
            @RequestParam String education,
            @RequestParam String position,
            @RequestParam String teachingCourse,
            @RequestParam String researchDirection
    ) {
        AdviserDO adviserDO = adviserService.getAdviser(id);
        adviserDO.setName(adviserName);
        adviserDO.setTel(adviserTel);
        adviserDO.setInfo(adviserInfo);
        adviserDO.setDepartment(department);
        adviserDO.setAvatar(avatar);
        adviserDO.setEmail(email);
        adviserDO.setOffice(office);
        adviserDO.setEducation(education);
        adviserDO.setPosition(position);
        adviserDO.setTeachingCourse(teachingCourse);
        adviserDO.setResearchDirection(researchDirection);
        adviserService.updateAdviserInfo(adviserDO);
        return RespUtil.success();
    }
}
