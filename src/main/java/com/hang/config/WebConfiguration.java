package com.hang.config;

import com.hang.CcsuServiceApplication;
import com.hang.handler.OpenIdResolver;
import com.hang.interceptors.AntiBrushInterceptor;
import com.hang.interceptors.HeaderCheckInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author hangs.zhang
 * @date 18-7-1
 * *****************
 * @function: 继承了该类之后 所有web配置自动失效
 */
@Configuration
@ComponentScan(basePackageClasses = CcsuServiceApplication.class, useDefaultFilters = true)
public class WebConfiguration implements WebMvcConfigurer {

    @Autowired
    private OpenIdResolver openIdResolver;

    @Autowired
    private HeaderCheckInterceptor headerCheckInterceptor;

    @Autowired
    private AntiBrushInterceptor antiBrushInterceptor;

    /**
     * 配置web静态资源位置
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        /// registry.addResourceHandler("/templates/**").addResourceLocations("classpath:/templates/");
    }

    /**
     * 配置拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(antiBrushInterceptor);
        registry.addInterceptor(headerCheckInterceptor)
                // 拦截
                .addPathPatterns("/feed/**")
                // .addPathPatterns("/information/**")
                .addPathPatterns("/team/**")
                .addPathPatterns("/project/**")
                .addPathPatterns("/user/**")
                .addPathPatterns("/session/**")
                .addPathPatterns("/api/**")
                // 不拦截
                .excludePathPatterns("/project/list")
                .excludePathPatterns("/project/addHonor2Project")
                .excludePathPatterns("/project/addSchedule2Project")
                .excludePathPatterns("/project/addPlan2Project")
                .excludePathPatterns("/team/getAllTeam")
                .excludePathPatterns("/team/createTeam")
                .excludePathPatterns("/team/addLog2Team")
                .excludePathPatterns("/team/addHonor2Team")
                .excludePathPatterns("/team/addMember2Team")
                .excludePathPatterns("/team/addProject2Team")
                .excludePathPatterns("/team/updateTeamInfo")
                .excludePathPatterns("/user/getUserInfoByOpenId")
                .excludePathPatterns("/user/getAdvisers")
                .excludePathPatterns("/user/insertAdviserInfo")
                .excludePathPatterns("/user/updateAdviserInfo")
                .excludePathPatterns("/information/listApply")
                .excludePathPatterns("/information/listActivity")
                .excludePathPatterns("/information/listAll")
                .excludePathPatterns("/information/createInformation")
                .excludePathPatterns("/information/removeInformation")
                .excludePathPatterns("/user/login")
                .excludePathPatterns("/school/**")
                .excludePathPatterns("/notification/**");
    }

    /**
     * 配置参数解析器
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(openIdResolver);
    }
}
