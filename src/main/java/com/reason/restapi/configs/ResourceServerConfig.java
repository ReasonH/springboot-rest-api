package com.reason.restapi.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    // Auth 서버랑 연동되어 사용됨
    // 토근 기반으로 인증정보를 확인하고 리소스 서버에 접근을 제한한다.

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("event");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .anonymous()
                    .and()
                .authorizeRequests()
                    .mvcMatchers(HttpMethod.GET, "/api/**")
                        .permitAll()
                    .anyRequest()
                        .authenticated()
                    .and()
                .exceptionHandling()
                    .accessDeniedHandler(new OAuth2AccessDeniedHandler());
    }
}
