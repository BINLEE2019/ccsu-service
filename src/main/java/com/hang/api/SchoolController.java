package com.hang.api;

import com.hang.annotation.OpenId;
import com.hang.aop.StatisticsTime;
import com.hang.enums.LostPropertyAndRecruitEnum;
import com.hang.enums.ResultEnum;
import com.hang.exceptions.ApiAssert;
import com.hang.exceptions.ApiException;
import com.hang.pojo.data.CourseDO;
import com.hang.pojo.data.LostPropertyAndRecruitDO;
import com.hang.pojo.data.StudentDO;
import com.hang.pojo.data.UserInfoDO;
import com.hang.pojo.vo.BaseRes;
import com.hang.pojo.vo.CourseVO;
import com.hang.pojo.vo.LostPropertyAndRecruitVO;
import com.hang.service.CourseUpdateService;
import com.hang.service.SchoolService;
import com.hang.service.StudentService;
import com.hang.service.UserService;
import com.hang.utils.RespUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author test
 * @date 19-4-28
 * *****************
 */
@Api("校园服务")
@RestController
@RequestMapping("/school")
public class SchoolController {

    @Autowired
    private SchoolService schoolService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private UserService userService;

    @Autowired
    private CourseUpdateService updateService;



    /**
     * 查询第n周的课表
     */
    @StatisticsTime("getCourseByWeek")
    @ApiOperation("查询第n周的课表,OpenId参数不用传")
    @GetMapping("/course/getCourseByWeek")
    public BaseRes getCourseByWeek(@OpenId String openId, @RequestParam Integer week,
                                   @RequestParam(required = false, defaultValue = "2019-2020-1") String semester) {
        ApiAssert.checkOpenId(openId);
        UserInfoDO userInfo = userService.getUserInfoByOpenId(openId);
        jwcAccountCheck(userInfo);
        List<CourseVO> courseVOS = schoolService.getCourseByWeek(userInfo.getJwcAccount(), week, semester);
        return RespUtil.success(courseVOS);
    }

    /**
     * 查询全部课表
     */
    @StatisticsTime("getAllCourse")
    @ApiOperation("查询全部课表,OpenId参数不用传")
    @GetMapping("/course/getAllCourse")
    public BaseRes getAllCourse(@OpenId String openId,
                                @RequestParam(required = false, defaultValue = "2019-2020-1") String semester) {
        ApiAssert.checkOpenId(openId);
        UserInfoDO userInfo = userService.getUserInfoByOpenId(openId);
        jwcAccountCheck(userInfo);
        List<CourseDO> allCourse = schoolService.getAllCourse(userInfo.getJwcAccount(), semester);
        return RespUtil.success(allCourse);
    }

    /**
     * 查询全部课表
     */
    @StatisticsTime("UpdateCourse")
    @ApiOperation("更新全部课表")
    @RequestMapping("/course/updateCourse")
    public BaseRes updateCourse(@OpenId String openId,
                                @RequestParam(required = false, defaultValue = "2019-2020-1") String semester) {
        ApiAssert.checkOpenId(openId);
        UserInfoDO userInfoDO=userService.getUserInfoByOpenId(openId);
        jwcAccountCheck(userInfoDO);
        StudentDO studentDO=studentService.getStudentInfoByOpenId(openId);
        Integer flag=updateService.turnToCourse(studentDO.getJwcAccount(),semester,studentDO.getCode());
        if (flag==1){
            return RespUtil.success();
        }
        else {
            return RespUtil.error(ResultEnum.COURSE_UPDATE_ERROR);
        }
    }

    @StatisticsTime("initiatorMessage")
    @ApiOperation("发布失物与招领 code=0为发布失物 code=1为发布招领,默认code为0")
    @GetMapping("/initiatorMessage")
    public BaseRes initiatorLostAndRecruitMessage(@ModelAttribute LostPropertyAndRecruitVO lostPropertyAndRecruitVO,
                                                  @RequestParam(required = false, defaultValue = "0") int code) {
        if (code == 0) {
            schoolService.saveLostPropertyAndRecruit(lostPropertyAndRecruitVO, LostPropertyAndRecruitEnum.LOSTPROPERTY);
        } else {
            schoolService.saveLostPropertyAndRecruit(lostPropertyAndRecruitVO, LostPropertyAndRecruitEnum.RECRUIT);
        }
        return RespUtil.success();
    }

    @StatisticsTime("listLostAndRecruitMessage")
    @ApiOperation("失物招领列表 code=0为查询失物 code=1为查询招领,默认code为0")
    @GetMapping("/listLostAndRecruitMessage")
    public BaseRes listLostAndRecruitMessage(@RequestParam(required = false, defaultValue = "0") int code,
                                             @RequestParam(required = false, defaultValue = "0") int start,
                                             @RequestParam(required = false, defaultValue = "100") int offset) {
        if (code == 0) {
            return RespUtil.success(schoolService.listLostPropertyAndRecruit(LostPropertyAndRecruitEnum.LOSTPROPERTY, start, offset));
        } else {
            return RespUtil.success(schoolService.listLostPropertyAndRecruit(LostPropertyAndRecruitEnum.RECRUIT, start, offset));
        }
    }

    @StatisticsTime("removeLostAndRecruitMessage")
    @ApiOperation("删除LostAndRecruitMessage")
    @GetMapping("/removeLostAndRecruitMessage")
    public BaseRes removeLostAndRecruitMessage(@OpenId String openId, @RequestParam Integer id) {
        ApiAssert.checkOpenId(openId);
        schoolService.removeLostAndRecruit(id);
        return RespUtil.success();
    }


    @StatisticsTime("modifyLostAndRecruitMessage")
    @ApiOperation("修改LostAndRecruitMessage")
    @GetMapping("/modifyLostAndRecruitMessage")
    public BaseRes modifyLostAndRecruitMessage(
            @OpenId String openId,
            @RequestParam Integer id,
            @RequestParam String initiatorMessage,
            @RequestParam String initiatorLocation,
            @RequestParam Long occurtime,
            @RequestParam String contactInformation) {
        ApiAssert.checkOpenId(openId);
        LostPropertyAndRecruitDO lostPropertyAndRecruitDO = schoolService.getLostAndRecruit(id);
        lostPropertyAndRecruitDO.setInitiatorMessage(initiatorMessage);
        lostPropertyAndRecruitDO.setInitiatorLocation(initiatorLocation);
        lostPropertyAndRecruitDO.setOccurTime(new Date(occurtime));
        lostPropertyAndRecruitDO.setContactInformation(contactInformation);
        schoolService.modifyLostAndRecruit(lostPropertyAndRecruitDO);
        return RespUtil.success();
    }


    @StatisticsTime("getReleaseLostPropertyandRecruit")
    @ApiOperation("查询自己发布的失物招领信息，code=0为查询失物 code=1为查询招领,默认code为0")
    @GetMapping("/getReleaseLostPropertyandRecruit")
    public BaseRes getReleaseLostPropertyandRecruit(@OpenId String openId, @RequestParam(required = false, defaultValue = "0") int code) {
        ApiAssert.checkOpenId(openId);
        UserInfoDO userInfo = userService.getUserInfoByOpenId(openId);
        jwcAccountCheck(userInfo);
        return RespUtil.success(schoolService.listLostPropertyAndRecruitSelf(userInfo.getJwcAccount()));
    }

    /**
     * 获取空闲教室
     */
    @StatisticsTime("getFreeClassroom")
    @ApiOperation("获取本学期空闲教室")
    @GetMapping("/getFreeClassroom")
    public BaseRes getFreeClassroom(@ApiParam("学期，默认为2019-2020-1") @RequestParam(required = false, defaultValue = "2017-2018-2") String semester,
                                    @ApiParam("课程时间节数 1-2或3-4或5-6等") String section,
                                    @ApiParam("周数") String week,
                                    @ApiParam("星期") String weekDay,
                                    @ApiParam("教学楼") String building) {
        return RespUtil.success(schoolService.getFreeClassroom(semester, section, week, weekDay, building));
    }


    @ApiOperation("学生列表")
    @GetMapping("/studentList")
    public BaseRes studentList(@RequestParam(required = false, defaultValue = "0") int start,
                               @RequestParam(required = false, defaultValue = "100") int offset) {
        return RespUtil.success(studentService.studentList(start, offset));
    }

    /**
     * 查询成绩
     */
    @StatisticsTime("getGrade")
    @ApiOperation("查询成绩")
    @GetMapping("/getGrade")
    public BaseRes getGrade(@OpenId String openId, @RequestParam(required = false, defaultValue = "2018-2019-2") String semeter) {
        ApiAssert.checkOpenId(openId);
        UserInfoDO userInfo = userService.getUserInfoByOpenId(openId);
        jwcAccountCheck(userInfo);
        StudentDO studentDO = studentService.getStudentInfoByOpenId(openId);
        return RespUtil.success(schoolService.getGrade(userInfo.getJwcAccount(), semeter, studentDO.getCode()));
    }

    /**
     * 学号确认,判断是否绑定了学号
     */
    private void jwcAccountCheck(UserInfoDO userInfo) {
        if (Objects.isNull(userInfo)) {
            throw new ApiException(ResultEnum.CAN_NOT_GET_USER_INFO);
        } else if (StringUtils.isEmpty(userInfo.getJwcAccount())) {
            throw new ApiException(ResultEnum.ACCOUNT_NOT_BIND);
        }
    }

}
