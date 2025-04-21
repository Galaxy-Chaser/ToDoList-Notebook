package org.example.todolistandnotebook.backend.config;

import org.example.todolistandnotebook.backend.interseptor.Interceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class Webconfig implements WebMvcConfigurer {

    @Autowired
    private Interceptor interceptor;

    @Override //add用于表示允许哪些路径访问，exclude用于表示哪些路径除外，/**表示全部路径, /*表示当前目录下的全部路径
// 调整排除路径，覆盖更多可能性
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/ws","/ws/**","/users/register","/users/getVerificationCode","/users/**","/frontend/**");
    }
}