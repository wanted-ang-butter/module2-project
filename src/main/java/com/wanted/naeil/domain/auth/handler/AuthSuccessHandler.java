package com.wanted.naeil.domain.auth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Collection;

public class AuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String role = authorities.iterator().next().getAuthority();

        String redirectUrl;
        switch (role) {
            case "ADMIN":
                redirectUrl = "/admin/dashboard";
                break;
            case "INSTRUCTOR":
                redirectUrl = "/instructor/dashboard";
                break;
            case "SUBSCRIBER":
            case "USER":
                redirectUrl = "/user/dashboard";
                break;
            default: // GUEST
                redirectUrl = "/guest/dashboard";
                break;
        }

        response.sendRedirect(redirectUrl);
    }
}
