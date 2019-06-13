package com.hx.imageserver.config;

import com.hx.imageserver.controller.ImageServerController;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @Author: AN
 * @Description: 映射配置
 * @Date: Created in 16:08 2019/6/10
 * @Modified by:
 */
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {

    /**
     * 配置图片的位置
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //配置静态资源处理
        registry.addResourceHandler("/imageServer/showImage/**")
                .addResourceLocations("file:"+ImageServerController.BASE_DIR.toString()+"\\");
        super.addResourceHandlers(registry);
    }


    /**
     * 跨域访问配置
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**").allowedMethods("*").allowedOrigins("*").allowedHeaders("*");
    }
}
