package com.reason.restapi.configs;

import com.reason.restapi.accounts.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    AccountService accountService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Bean
    public TokenStore tokenStore() {
        return new InMemoryTokenStore();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(accountService)
                .passwordEncoder(passwordEncoder);
    }

    // 스프링 시큐리티 외부에서 처리
    // 스태틱 메서드는 서버 부하를 줄이기 위해 web에서 거르는게 좋다고 본다?..
    @Override
    public void configure(WebSecurity web) throws Exception {
        // 정적 리소스 무시
        web.ignoring().mvcMatchers("/docs/index.html");
        // PathRequest를 사용하면 스프링 정적 요소 위치에 대해 한번에 처리가능
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

//    // 스프링 시큐리티 내에서 필터 처리
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests()
//                .mvcMatchers("/docs/index.html").anonymous()
//                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).anonymous();
//    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .anonymous() // 익명 사용자 허용
                .and()
                .formLogin() // form 인증 사용
                .and()
                .authorizeRequests()
                .mvcMatchers(HttpMethod.GET, "/api/**").authenticated() // 다음 api 요청은 익명허용
                .anyRequest().authenticated(); // 나머지는 인증 필요
    }
}
