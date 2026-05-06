package com.example.javaweb_congthongtinbenhvien.config;

import com.example.javaweb_congthongtinbenhvien.security.LoginInterceptor;
import com.example.javaweb_congthongtinbenhvien.security.RoleInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;
    private final RoleInterceptor roleInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(loginInterceptor)
                .addPathPatterns(
                        "/admin/**",
                        "/doctor/**",
                        "/patient/**"
                )
                .excludePathPatterns(
                        "/auth/**",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/error/**"
                );

        registry.addInterceptor(roleInterceptor)
                .addPathPatterns(
                        "/admin/**",
                        "/doctor/**",
                        "/patient/**"
                )
                .excludePathPatterns(
                        "/auth/**",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/error/**"
                );
    }
}