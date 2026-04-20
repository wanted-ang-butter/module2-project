package com.wanted.naeil.global.config;

import com.wanted.naeil.global.auth.handler.AuthSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;

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
    public SecurityFilterChain configure(HttpSecurity http,
                                         SessionRegistry sessionRegistry,
                                         AuthSuccessHandler authSuccessHandler) throws Exception {

        http.authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/auth/login", "/auth/signup", "/auth/fail", "/auth/find-id", "/auth/find-password",  "/", "/dashboard", "/dashboard/guest", "/course/**", "/community/**", "/error").permitAll();
                    auth.requestMatchers("/admin/**").hasAnyAuthority("ADMIN");
                    auth.requestMatchers("/instructor/**").hasAnyAuthority("ADMIN", "INSTRUCTOR");
                    auth.requestMatchers("/subscribe/**").hasAnyAuthority("ADMIN", "INSTRUCTOR", "SUBSCRIBER");
                    auth.requestMatchers("/user/**").hasAnyAuthority("ADMIN", "INSTRUCTOR", "SUBSCRIBER", "USER");
                    auth.requestMatchers("/guest/**").hasAnyAuthority("ADMIN", "INSTRUCTOR", "SUBSCRIBER", "USER", "GUEST");
                    auth.requestMatchers("/dashboard/admin").hasAnyAuthority("ADMIN");
                    auth.requestMatchers("/dashboard/instructor").hasAnyAuthority("ADMIN", "INSTRUCTOR");
                    auth.requestMatchers("/dashboard/user").hasAnyAuthority("ADMIN", "INSTRUCTOR", "SUBSCRIBER", "USER");
                    auth.anyRequest().authenticated();
                })
                .requestCache(cache -> cache.disable())
                .formLogin(login -> {
                    login.loginPage("/auth/login");
                    login.usernameParameter("user");
                    login.passwordParameter("pass");
                    login.failureUrl("/auth/login?error=true");
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