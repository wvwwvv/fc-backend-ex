package com.fc.fcseoularchive.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String property = System.getProperty("user.dir");
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:"+property+"/upload/");
    }

//  해당 cors 설정은 Security 에서 대체
//   왜? -> 해당 cors 가 실행 되기도 전에 SecurityFilter 에서 막힐 수도 있기 때문이다.
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//
//                // 나중에 운영 환경에서는 지정해주기 지금 개발할땐 풀어주기
//                //.allowedOrigins("http://localhost:3000", "https://your-frontend.com")
//
//                .allowedOriginPatterns("*")
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
//                .allowedHeaders("*");
//                // .allowCredentials(true); Jwt 쓰기때문에 비활성화
//    }
}
