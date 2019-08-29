package com.hang.dao;

import com.hang.pojo.data.ProjectDO;
import com.hang.pojo.data.TeamDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author hangs.zhang
 * @date 2019/1/26
 * *****************
 * function:
 */
@Repository
public interface TeamDAO {

    /**
     * 插入team
     */
    int insertTeam(TeamDO teamPO);

    /**
     * 插入team与user的映射
     */
    int insert2TeamUser(@Param("teamId") int teamId, @Param("userId") String userId);

    /**
     * 插入project与team的映射
     */
    int insert2TeamProject(@Param("teamId") int teamId, @Param("projectId") int projectId);


    /**
     * 插入导师信息与team映射
     */
    int insert2TeamAdviser(@Param("teamId") int teamId, @Param("AdviserId") int adviserId);

    /**
     * 根据teamId查询team
     */
    TeamDO selectByTeamId(int teamId);

    /**
     * 更新team
     */
    boolean updateTeam(TeamDO teamPO);

    /**
     * 删除team
     */
    boolean deleteTeamByTeamId(int teamId);

    /**
     * 查询team所属的project
     */
    List<ProjectDO> selectProjectByTeamId(int teamId);

    /**
     * 查询project依附team
     */
    List<TeamDO> selectTeamByProjectId(int projectId);

    /**
     * 查询user对应的团队
     */
    List<TeamDO> selectTeamByUserId(String userId);

    List<TeamDO> selectTeams(@Param("start") int start, @Param("offset") int offset);

    List<TeamDO> selectTeamsByAdvisor(String advisor);

}
