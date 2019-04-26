package com.hang.api;

import com.hang.pojo.vo.BaseRes;
import com.hang.pojo.vo.ProjectVO;
import com.hang.service.ProjectService;
import com.hang.utils.RespUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hangs.zhang
 * @date 2019/1/25
 * *****************
 * function:
 */
@Api("项目相关接口")
@RequestMapping("/project")
@RestController
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    /**
     * 根据projectId查询项目
     *
     * @param projectId
     * @return
     */
    @ApiOperation("根据projectId获取项目信息")
    @GetMapping("/getProjectByProjectId")
    public BaseRes getProjectByProjectId(@RequestParam int projectId) {
        ProjectVO projectVO = projectService.getProjectByProjectId(projectId);
        return RespUtil.success(projectVO);
    }

    /**
     * 基本信息管理，即修改基本信息
     *
     * @param projectId
     * @param name
     * @param description
     * @param properties
     * @return
     */
    @ApiOperation("更新项目基本信息")
    @GetMapping("/updateProject")
    public BaseRes updateProject(int projectId, String name, String description, String properties) {
        projectService.updateProject(projectId, name, description, properties);
        return RespUtil.success();
    }

    /**
     * 增加honor
     *
     * @param projectId
     * @param honor
     * @return
     */
    @ApiOperation("为项目添加荣誉")
    @GetMapping("/addHonor2Project")
    public BaseRes addHonor2Project(int projectId, String honor) {
        projectService.addHonor2Project(projectId, honor);
        return RespUtil.success();
    }

    /**
     * 追加最新进展
     *
     * @param projectId
     * @param schedule
     * @return
     */
    @ApiOperation("为项目追加进度")
    @GetMapping("/addSchedule2Project")
    public BaseRes addSchedule2Project(int projectId, String schedule) {
        projectService.addSchedule2Project(projectId, schedule);
        return RespUtil.success();
    }

}
