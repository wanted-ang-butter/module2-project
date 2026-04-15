package com.wanted.naeil.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 브라우저에서 /uploads/** 로 요청이 오면
        registry.addResourceHandler("/uploads/**")
                // 실제 파일 시스템의 uploadDir 위치에서 파일을 찾도록 설정합니다.
                // file:/// 접두사가 반드시 필요합니다.
                .addResourceLocations("file:" + uploadDir);
    }
}
