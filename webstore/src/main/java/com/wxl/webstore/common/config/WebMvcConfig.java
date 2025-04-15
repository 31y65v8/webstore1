package com.wxl.webstore.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.io.File;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${upload.product-images.path}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 确保路径以文件分隔符结尾
        String path = uploadPath.endsWith(File.separator) ? uploadPath : uploadPath + File.separator;
        
        registry.addResourceHandler("/api/product/images/**")
                .addResourceLocations("file:" + path + "/");
    }
} 