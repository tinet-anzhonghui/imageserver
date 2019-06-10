package com.hx.imageserver.config;

import com.hx.imageserver.controller.ImageServerController;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @Author: AN
 * @Description: 配置图片的位置
 * @Date: Created in 16:08 2019/6/10
 * @Modified by:
 */
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //配置静态资源处理
        registry.addResourceHandler("/showImage/**")
                .addResourceLocations("file:"+ImageServerController.BASE_DIR.toString()+"\\");
        super.addResourceHandlers(registry);
    }
}
