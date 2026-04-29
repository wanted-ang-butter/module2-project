package com.wanted.naeil.global.config;

import com.wanted.naeil.global.auth.handler.AuthFailureHandler;
import com.wanted.naeil.global.auth.handler.AuthSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@EnableMethodSecurity // @PreAuthorize 활성화위한 어노테이셔
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public AuthSuccessHandler authSuccessHandler() {
        return new AuthSuccessHandler();
    }

    @Bean
    public AuthFailureHandler authFailureHandler() {
        return new AuthFailureHandler();
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http,
                                         SessionRegistry sessionRegistry,
                                         AuthSuccessHandler authSuccessHandler,
                                         AuthFailureHandler authFailureHandler) throws Exception {

        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers(
                    "/auth/login",
                    "/auth/signup",
                    "/auth/fail",
                    "/auth/find-id",
                    "/auth/find-password",
                    "/",
                    "/dashboard",
                    "/dashboard/guest",
                    "/course/**",
                    "/subscription/**",
                    "/community/**",
                    "/error",
                    "/uploads/**"
            ).permitAll();

            // 관리자 전용
            auth.requestMatchers("/admin/**")
                    .hasAnyAuthority("ADMIN");

            // 강사 이상
            auth.requestMatchers("/instructor/**")
                    .hasAnyAuthority("ADMIN", "INSTRUCTOR");

            // 구독자 이상
            auth.requestMatchers("/subscribe/**")
                    .hasAnyAuthority("ADMIN", "INSTRUCTOR", "SUBSCRIBER");

            // 유저 이상
            auth.requestMatchers("/user/**")
                    .hasAnyAuthority("ADMIN", "INSTRUCTOR", "SUBSCRIBER", "USER");

            // 마이페이지 / 내 강의 / 결제 / 장바구니 등 로그인 사용자 이상
            auth.requestMatchers("/my-courses/**")
                    .hasAnyAuthority("ADMIN", "INSTRUCTOR", "SUBSCRIBER", "USER");

            // 실시간 강의 예약, 취소는 유저 이상
            auth.requestMatchers(HttpMethod.POST, "/live-lecture/*/reservations/**")
                    .hasAnyAuthority("ADMIN", "INSTRUCTOR", "SUBSCRIBER", "USER");

            // 실시간 강의 입장은 유저 이상
            auth.requestMatchers("/live-lecture/*/room")
                    .hasAnyAuthority("ADMIN", "INSTRUCTOR", "SUBSCRIBER", "USER");

            // 유저 대시보드
            auth.requestMatchers("/dashboard/user")
                    .hasAnyAuthority("ADMIN", "INSTRUCTOR", "SUBSCRIBER", "USER");

            // 강사 대시보드
            auth.requestMatchers("/dashboard/instructor")
                    .hasAnyAuthority("ADMIN", "INSTRUCTOR");

            // 관리자 대시보드
            auth.requestMatchers("/dashboard/admin")
                    .hasAnyAuthority("ADMIN");

            auth.requestMatchers("/cart/**")
                    .hasAnyAuthority("ADMIN", "INSTRUCTOR", "SUBSCRIBER", "USER");

            auth.requestMatchers("/payment/**")
                    .hasAnyAuthority("ADMIN", "INSTRUCTOR", "SUBSCRIBER", "USER");

            auth.requestMatchers("/payments/**")
                    .hasAnyAuthority("ADMIN", "INSTRUCTOR", "SUBSCRIBER", "USER");

            auth.requestMatchers("/enrollments/**")
                    .hasAnyAuthority("ADMIN", "INSTRUCTOR", "SUBSCRIBER", "USER");

            // 게스트 영역은 전부 접근 가능하게 둘 거면 permitAll 권장
            auth.requestMatchers("/guest/**")
                    .permitAll();

            auth.anyRequest().authenticated();
        }).requestCache(cache -> cache.disable())
                .formLogin(login -> {
                    login.loginPage("/auth/login");
                    login.usernameParameter("user");
                    login.passwordParameter("pass");
                    login.failureHandler(authFailureHandler);
                    login.successHandler(authSuccessHandler); // defaultSuccessUrl 대신
                }).rememberMe(rememberMe -> {
                    rememberMe.rememberMeParameter("remember-me");
                    rememberMe.tokenValiditySeconds(86400);
                    rememberMe.key("remember-me-secret-key");
                }).logout(logout -> {
                    logout.logoutUrl("/auth/logout");
                    logout.deleteCookies("JSESSIONID");
                    logout.deleteCookies("remember-me");
                    logout.invalidateHttpSession(true);
                    logout.logoutSuccessUrl("/");
                }).sessionManagement(session -> {
                    session.maximumSessions(1).sessionRegistry(sessionRegistry);
                    session.invalidSessionUrl("/");
                })
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}