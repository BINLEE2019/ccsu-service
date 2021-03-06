package com.hang.api;

import com.fasterxml.jackson.annotation.JsonView;
import com.hang.aop.StatisticsTime;
import com.hang.exceptions.ApiException;
import com.hang.pojo.data.InformationDO;
import com.hang.pojo.vo.BaseRes;
import com.hang.service.InformationService;
import com.hang.utils.RespUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

import static com.hang.constant.InformationConstant.CATEGORY_MAP;

/**
 * @author hangs.zhang
 * @date 2019/1/28
 * *****************
 * function:
 */
@Api("首页feed流")
@RequestMapping("/feed")
@RestController
public class FeedController {

    @Autowired
    private InformationService informationService;

    /**
     * 最新的10条
     * @apiNote 这里默认查询最新的消息只有招新和通知两个类别
     *
     * @return
     */
    @StatisticsTime("latest")
    @ApiOperation("请求最新得十条feed流数据")
    @JsonView(InformationDO.SimpleInformation.class)
    @GetMapping("/latest")
    public BaseRes latest() {
        List<InformationDO> latestInformation = informationService.getLatestInformation();
        return RespUtil.success(latestInformation);
    }

    /**
     * 最热
     *
     * @return
     */
    @StatisticsTime("hot")
    @ApiOperation("请求点击最多的feed流数据")
    @JsonView(InformationDO.SimpleInformation.class)
    @GetMapping("/hot")
    public BaseRes hot() {
        List<InformationDO> hotInformation = informationService.getHotInformation();
        if (hotInformation == null || hotInformation.size() == 0) {
            return RespUtil.success(informationService.getLatestInformation());
        }
        return RespUtil.success(hotInformation);
    }

    /**
     * 根据类别查询
     *
     * @param category
     * @param start
     * @param offset
     * @return
     */
    @StatisticsTime("listByCategory")
    @ApiOperation("根据类别查询feed流数据")
    @JsonView(InformationDO.SimpleInformation.class)
    @GetMapping("/listByCategory")
    public BaseRes listByCategory(@RequestParam String category,
                                  @RequestParam(required = false, defaultValue = "0") int start,
                                  @RequestParam(required = false, defaultValue = "100") int offset) {
        if (!CATEGORY_MAP.containsKey(category)) {
            throw new ApiException(-1, "category不存在");
        }
        List<InformationDO> informations = informationService.getInformationByCategory(category, start, offset);
        Collections.reverse(informations);
        return RespUtil.success(informations);
    }

}
